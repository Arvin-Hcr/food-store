package com.hcr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//@Controller   在MVC中长用
@ApiIgnore  //忽略显示
@RestController   //包含了所有，返回默认为json
@RequestMapping("redis")
public class RedisController {

    final private static Logger logger = LoggerFactory.getLogger(RedisController.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/set")
    public Object set(String key, String value) {
            redisTemplate.opsForValue().set(key,value);
        return "OK";

    }


    @GetMapping("/get")
    public Object get(String key) {

        return redisTemplate.opsForValue().get(key);

    }
    @GetMapping("/del")
    public Object del(String key) {
        redisTemplate.delete(key);
        return "OK";

    }
}
