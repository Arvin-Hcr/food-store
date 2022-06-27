package com.hcr.center;

import com.hcr.bo.center.CenterUserBO;
import com.hcr.pojo.Users;
import com.hcr.resource.FileUpload;
import com.hcr.service.center.CenterUserService;
import com.hcr.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "用户信息接口", tags = {"用户信息相关接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUsersController {

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileUpload fileUpload;

    @ApiOperation(value = "用户头像修改", notes = "用户头像修改", httpMethod = "POST")
    @PostMapping("uploadFace")
    public JSONResult uploadFace(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "file", value = "用户头像", required = true)
            MultipartFile file,
            HttpServletRequest request,HttpServletResponse response) throws IOException {

        //定义用户头像地址
        String fileSpace = fileUpload.getImageUserFaceLocation();

        //在路径上为每一个用户增加一个userid，用于区分用户上传
        //File.separator 代替分隔符 "/" 因 Windows 与 Linux 不相同  D:\\fhb\\test\\" 默认使用系统分隔符
        String uploadPathPrefix = File.separator + userId;

        //此方法在本地Windows路径使用
        String uploadPathPrefixWin = "/" + userId;

        //开始文件上传
        if (file != null){
            FileOutputStream fileOutputStream = null;
            try {

                //获得文件上传的文件名称，原始文件名 getOriginalFilename
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)){

                    //文件重命名 123-face.png -> ["123-face","png"]
                    String fileNameArr[] = fileName.split("\\.");

                    //获取文件的后缀名  索引下标是以0开始的，所以-1
                    String suffix = fileNameArr[fileNameArr.length - 1];

                    //图片限制格式以防后门  黑客会绕过前端直接访问接口，向服务器上传一些  .sh  .php 文件，成功后对方可以通过浏览器直接访问相应的文件
                    if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg")){
                        return JSONResult.errorMsg("图片格式不正确!");
                    }

                    //face-{userid}.png
                    //文件名程重组 覆盖试上传， 增量式上传：额外拼接当前时间
                    String newFileName = "face-" + userId + DateUtils.getTimeNum() + "." + suffix;
                    //上传的头像最终保存的位置
                    String finalFacePath = fileSpace + uploadPathPrefix + File.separator + newFileName;

                    //用于提供web服务访问的地址(本地)
                    //uploadPathPrefixWin += ("/" + newFileName);

                    //生产用于提供web服务访问的地址
                    uploadPathPrefix += ("/" + newFileName);

                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null) {
                        // 创建文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    //文件输出保存到目录
                    fileOutputStream = new FileOutputStream(outFile);
                    InputStream inputStream = file.getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 获取图片服务地址
        String imageServerUrl = fileUpload.getImageServerUrl();

        // 由于浏览器可能存在缓存的情况，所以需要加上时间戳来保证更新后的图片可以及时刷新
       // String finalUserFaceUrl = imageServerUrl + uploadPathPrefixWin
         //       + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);
        //生产
        String finalUserFaceUrl = imageServerUrl + uploadPathPrefix
                + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);

        // 更新用户头像到数据库
        Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);

        userResult = setNullProperty(userResult);
         CookieUtils.setCookie(request, response, "user",
                 JsonUtils.objectToJson(userResult), true);

        // TODO 后续要改，增加令牌token，会整合进redis，分布式会话

        return JSONResult.ok();
    }

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("update")
    public JSONResult update(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult result,
            HttpServletResponse response, HttpServletRequest request){

        // @Valid 验证注解     BindingResult result -> 错误信息
        // 判断BindingResult是否保存错误的验证信息，如果有，则直接return
        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return JSONResult.errorMap(errorMap);
        }

        Users users = centerUserService.updateUserInfo(userId, centerUserBO);

        users = setNullProperty(users);
        CookieUtils.setCookie(request,response,"user",
                                JsonUtils.objectToJson(users),true);

        //TODO 后续要改，增加令牌token，会整合进redis，分布式会话
        return JSONResult.ok();
    }

    private Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError error : errorList) {
            // 发生验证错误所对应的某一个属性
            String errorField = error.getField();
            // 验证错误的信息
            String errorMsg = error.getDefaultMessage();

            map.put(errorField, errorMsg);
        }
        return map;
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }
}
