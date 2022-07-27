package com.hcr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * (exclude = {SecurityAutoConfiguration.class})
 * 因为引入了spring的安全框架，
 * 为了避免清除cookie中的session时重新登录session认证， 密码在控制台中找
 * 所以排除权限的自动装配
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})   //包含开启事务
//扫描mybatis通用所在的包
@MapperScan(basePackages = "com.hcr.mapper")
public class ApplicationShop {

    public static void main(String[] args) {

        SpringApplication.run(ApplicationShop.class,args);
    }
}
