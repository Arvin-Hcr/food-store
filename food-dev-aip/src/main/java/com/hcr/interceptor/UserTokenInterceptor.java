package com.hcr.interceptor;

import com.hcr.controller.TestController;
import com.hcr.utils.JSONResult;
import com.hcr.utils.JsonUtils;
import com.hcr.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class UserTokenInterceptor implements HandlerInterceptor {

    final private static Logger logger = LoggerFactory.getLogger(UserTokenInterceptor.class);

    public static final String REDIS_USER_TOKEN = "REDIS_USER_TOKEN";

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 拦截请求，在访问controller调用之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.err.println("进入到拦截器...");

        /**
         * 拦截机制-->从redis中获取的token与前端用户传递过来的token做对比，若一致则放行
         */
        //与前端约定好，从前端Header中获取的
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        if (StringUtils.isNoneBlank(userId) && StringUtils.isNoneBlank(userToken)){
            String uniqueToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isBlank(uniqueToken)){
                returnErrorResponse(response,JSONResult.errorMsg("请登录..."));
                logger.warn("请登录...");
            }else {
                if (!uniqueToken.equals(userToken)){
                    //不退出登录token会变化
                    returnErrorResponse(response,JSONResult.errorMsg("账号在异地登录..."));
                    logger.warn("账号在异地登录...");
                    return false;
                }
            }
        }else {
            returnErrorResponse(response,JSONResult.errorMsg("请登录..."));
            logger.warn("请登录...");
            return false;
        }


        /**
         * false：请求被拦截，被驳回，验证出现问题
         * true：请求在经过验证以后，是OK的，是可以放行的
         */
        return true;
    }

    //通过 HttpServletResponse response 前端可以接收到jsonResult
    public void returnErrorResponse(HttpServletResponse response, JSONResult result){
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/json");
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (out != null)
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 请求访问controller之后，渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求访问controller之后，渲染视图之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
