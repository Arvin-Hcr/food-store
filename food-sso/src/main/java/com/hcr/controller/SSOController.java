package com.hcr.controller;

import com.hcr.pojo.Users;
import com.hcr.service.UserService;
import com.hcr.utils.JSONResult;
import com.hcr.utils.JsonUtils;
import com.hcr.utils.MD5Utils;
import com.hcr.utils.RedisOperator;
import com.hcr.vo.UsersVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

//@Controller   在MVC中长用

@Controller   //包含了所有，返回默认为json
public class SSOController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "REDIS_USER_TOKEN";

    @GetMapping("/login")
    public String login(String returnUrl, Model model, HttpServletRequest request, HttpServletResponse response) {

        model.addAttribute("returnUrl",returnUrl);

        //TODO 后续完善校验是否登录
        //用户从未登录过，第一次进入跳转到CAS的统一登录页面
        return "login";

    }

    /**
     * 用户登录成功，实现全局会话
     * @param username
     * @param password
     * @param returnUrl
     * @param model
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/doLogin")
    public String login(String username,
                        String password,
                        String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) throws Exception {

        model.addAttribute("returnUrl",returnUrl);

        //1. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||StringUtils.isBlank(password) ){
            model.addAttribute("errmsg","用户名或密码不能为空");
            return "login";
        }

        //2.实现登录

        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

        if (userResult == null){
            model.addAttribute("errmsg","用户名或密码不正确");
            return "login";
        }
        //实现用户redis会话，也可使用其他中间件
        String uniqueToken = UUID.randomUUID().toString().trim();  //可使用base64进行加密来作为token
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult,usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(), JsonUtils.objectToJson(usersVO)); //不会过期，永久有效
        return "login";

    }


}
