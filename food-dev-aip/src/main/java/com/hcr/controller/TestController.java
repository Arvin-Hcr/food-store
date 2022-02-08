package com.hcr.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

//@Controller   在MVC中长用
@ApiIgnore  //忽略显示
@RestController   //包含了所有，返回默认为json
public class TestController {

    //final private static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/hello")
    public Object hello() {

        return "Hello World~";

    }

    @GetMapping("/test")
    public Object test(){
        return "This is Test!";
    }

}
