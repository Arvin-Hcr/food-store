package com.hcr.center;

import com.hcr.bo.center.CenterUserBO;
import com.hcr.pojo.Users;
import com.hcr.service.center.CenterUserService;
import com.hcr.utils.CookieUtils;
import com.hcr.utils.JSONResult;
import com.hcr.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "用户信息接口", tags = {"用户信息相关接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUsersController {

    @Autowired
    private CenterUserService centerUserService;

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("update")
    public JSONResult update(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @RequestBody CenterUserBO centerUserBO,
            HttpServletResponse response, HttpServletRequest request){

        Users users = centerUserService.updateUserInfo(userId, centerUserBO);

        users = setNullProperty(users);
        CookieUtils.setCookie(request,response,"user",
                                JsonUtils.objectToJson(users),true);

        //TODO 后续要改，增加令牌token，会整合进redis，分布式会话
        return JSONResult.ok();
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
