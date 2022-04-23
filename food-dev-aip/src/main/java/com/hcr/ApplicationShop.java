package com.hcr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication   //包含开启事务
//扫描mybatis通用所在的包
@MapperScan(basePackages = "com.hcr.mapper")
//开启事务管理
//@EnableTransactionManagement  自动装配
@EnableScheduling       // 开启定时任务
public class ApplicationShop {

    public static void main(String[] args) {

        SpringApplication.run(ApplicationShop.class,args);
    }
}
