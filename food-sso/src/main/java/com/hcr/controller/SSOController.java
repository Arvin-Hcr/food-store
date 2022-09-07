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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
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
    public static final String REDIS_USER_TICKET = "redis_user_ticket";
    public static final String REDIS_TMP_TICKET = "redis_tmp_ticket";
    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";


    @GetMapping("/login")
    public String login(String returnUrl, Model model, HttpServletRequest request, HttpServletResponse response) {

        model.addAttribute("returnUrl",returnUrl);

        // 1. 获取userTicket门票，如果cookie中能够获取到，证明用户登录过，此时签发一个一次性的临时票据并且回跳
        String userTicket = getCookie(request, COOKIE_USER_TICKET);

        boolean isVerified = verifyUserTicket(userTicket);
        if (isVerified) {
            String tmpTicket = createTempTicket();
            return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
        }

        // 2. 用户从未登录过，第一次进入则跳转到CAS的统一登录页面
        return "login";

    }

    /**
     * 校验CAS全局用户门票
     * @param userTicket
     * @return
     */
    private boolean verifyUserTicket(String userTicket) {

        // 0. 验证CAS门票不能为空
        if (StringUtils.isBlank(userTicket)) {
            return false;
        }

        // 1. 验证CAS门票是否有效
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return false;
        }

        // 2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return false;
        }

        return true;
    }

    /**
     * CAS的统一登录接口
     *      目的：
     *          1、登录后创建用户的全局会话                      --> uniqueToken
     *          2、创建用户全局门票，用以表示在CAS端是否登录      --> userTicket
     *          3、创建用户的临时票据，用于回跳回传              --> tmpTicket
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

        //3.实现用户redis会话，也可使用其他中间件
        String uniqueToken = UUID.randomUUID().toString().trim();  //可使用base64进行加密来作为token
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult,usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(), JsonUtils.objectToJson(usersVO)); //不会过期，永久有效

        //3.生成ticket门票，全局门票，代表用户在CAS端登录过
        String userTicket = UUID.randomUUID().toString().trim();
        //3.1 用户全局门票需要放入CAS端的cookie中,当用户登录其他站点的时候从cookie中获取
        setCookie(COOKIE_USER_TICKET,userTicket,response);

        //4.userTicket关联用户id，并且放入redis中，代表这个用户有门票，可以进入所有系统登录
        redisOperator.set(REDIS_USER_TICKET + ":" + userTicket,userResult.getId());

        //5.生产临时门票，回跳到调用端网站，是由CAS端所签发的一个一次性的临时ticket
        String tmpTicket = createTempTicket();

        /**
         * userTicket:用于表示用户在CAS端的一个登录状态：已经登录
         * tmpTicket:用于颁发给用户进行一次性的验证的票据，有时效性
         */
        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;



       // return "login";

    }

    /**
     * 销毁临时票据
     * @param tmpTicket
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public JSONResult verifyTmpTicket(String tmpTicket,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        //使用一次性临时票据来验证用户是否登录，如果登录过，把用户会话信息返回给站点
        //使用完毕后，需要销毁临时票据
        String tmpTicketValue = redisOperator.get(REDIS_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(tmpTicketValue)){
            return JSONResult.errorUserTicket("用户票据过期");
        }
        //如果临时票据OK，则需要销毁，并且拿到CAS端cookie中的全局userTicket，从此再获取用户会话
        if (!tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))){
            return JSONResult.errorUserTicket("用户票据异常");
        }else {
            //销毁临时票据
            redisOperator.del(REDIS_TMP_TICKET + ":" + tmpTicket);
        }
        // 1. 验证并且获取用户的userTicket
        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorUserTicket("用户票据异常");
        }

        // 2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return JSONResult.errorUserTicket("用户票据异常");
        }

        // 验证成功，返回OK，携带用户会话
        return JSONResult.ok(JsonUtils.jsonToPojo(userRedis, UsersVO.class));
    }

    /**
     * 创建临时票据
     * @return
     */
    private String createTempTicket(){
        String tmpTicket = UUID.randomUUID().toString().trim();
        try {
            redisOperator.set(REDIS_TMP_TICKET + ":" + tmpTicket,MD5Utils.getMD5Str(tmpTicket),600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpTicket;
    }

    private void setCookie(String key,String val,HttpServletResponse response){
        Cookie cookie = new Cookie(key,val);
        cookie.setDomain("sso.com"); //域
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String getCookie(HttpServletRequest request, String key) {

        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || StringUtils.isBlank(key)) {
            return null;
        }

        String cookieValue = null;
        for (int i = 0 ; i < cookieList.length; i ++) {
            if (cookieList[i].getName().equals(key)) {
                cookieValue = cookieList[i].getValue();
                break;
            }
        }

        return cookieValue;
    }

}
