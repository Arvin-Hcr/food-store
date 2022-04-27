package com.hcr.controller;

import com.hcr.bo.UserBO;
import com.hcr.pojo.Users;
import com.hcr.service.UserService;
import com.hcr.utils.CookieUtils;
import com.hcr.utils.JSONResult;
import com.hcr.utils.JsonUtils;
import com.hcr.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "注册登陆", tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public JSONResult usernameIsExist(@RequestParam String username) {
        //1. 判断用户名是否为空
        if (StringUtils.isBlank(username)) {
            return JSONResult.errorMsg("用户名为空");

        }
        //2.查找注册的用户是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return JSONResult.errorMsg("用户名已存在");
        }
        //3. 请求成功，用户名不存在
        return JSONResult.ok();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/regist")
    public JSONResult usernameIsExist(@RequestBody UserBO userBO,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPwd = userBO.getConfirmPassword();

        //1. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username)
                || StringUtils.isBlank(password)
                || StringUtils.isBlank(confirmPwd)) {
            return JSONResult.errorMsg("用户名或密码不能为空");
        }
        //2.查询用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return JSONResult.errorMsg("用户名已经存在");
        }
        //3.密码长度不能少于6位
        if (password.length() < 6) {
            return JSONResult.errorMsg("密码长度不能小于6");
        }
        //4.判断两次密码是否一致
        if (!password.equals(confirmPwd)) {
            return JSONResult.errorMsg("两次密码输入不一致");
        }
        //5.实现注册
        Users userResult = userService.createUser(userBO);

        CookieUtils.setCookie(request,response,"user",
                JsonUtils.objectToJson(userResult),true);


        return JSONResult.ok();

    }

    @ApiOperation(value = "用户登录",notes = "用户登录",httpMethod = "POST")
    @PostMapping("/login")
    public JSONResult login(@RequestBody UserBO userBO,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        //1. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||StringUtils.isBlank(password) ){
            return JSONResult.errorMsg("用户名或密码不能为空");
        }

        //2.实现登录

        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

        if (userResult == null){
            return JSONResult.errorMsg("用户名或密码不正确");
        }

        //user->key  userResult->value
        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request,response,"user",
                JsonUtils.objectToJson(userResult),true);

        return JSONResult.ok(userResult);

    }

    /**
     * 返回前端，将用户私密信息设为空(保存在cookie中不安全)
     * @param userResult
     * @return
     */
    private Users setNullProperty(Users userResult){
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }

    @ApiOperation(value = "用户退出登录",notes = "用户退出登录",httpMethod = "POST")
    @PostMapping("/logout")
    public JSONResult logout(@RequestParam String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response){

        //清除用户的相关信息的cookie
        CookieUtils.deleteCookie(request,response,"user");
        //TODO 用户退出需要清空购物车
        //TODO 分布式会话中需要清除用户数据

        return JSONResult.ok();
    }


}
