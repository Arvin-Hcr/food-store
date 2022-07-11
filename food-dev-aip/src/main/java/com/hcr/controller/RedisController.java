package com.hcr.controller;

import com.hcr.utils.RedisOperator;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@Controller   在MVC中长用
@ApiIgnore  //忽略显示
@RestController   //包含了所有，返回默认为json
@RequestMapping("redis")
public class RedisController {

    final private static Logger logger = LoggerFactory.getLogger(RedisController.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisOperator redisOperator;

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

    /**
     * 大量的key
     * @param key
     * @return
     */
    @GetMapping("/getALot")
    public Object getALot(String... key) {
        List<String> result = new ArrayList<>();
        for (String k : key){
            result.add(redisOperator.get(k));
        }
        return result;

    }

    /**
     * 批量查询 mget
     * @param keys
     * @return
     */
    @GetMapping("/mget")
    public Object mget(String... keys) {
        List<String> keysList = Arrays.asList(keys); //将string数组转为list
        return redisOperator.mget(keysList);

    }
}
