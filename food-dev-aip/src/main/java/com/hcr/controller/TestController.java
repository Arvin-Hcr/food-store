package com.hcr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//@Controller   在MVC中长用
@ApiIgnore  //忽略显示
@RestController   //包含了所有，返回默认为json
public class TestController {

    final private static Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/hello")
    public Object hello() {
        logger.debug("debug: hello~");
        logger.info("debug: hello~");
        logger.warn("debug: hello~");
        return "Hello World~";

    }

    @GetMapping("/test")
    public Object test(){
        return "This is Test!";
    }

    @GetMapping("/setSession")
    public Object setSession(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("userInfo","new user");
        session.setMaxInactiveInterval(3600);
        session.getAttribute("userInfo");
        return "ok";
    }
}
