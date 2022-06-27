package com.hcr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    public CorsConfig() {
    }

    @Bean
    public CorsFilter corsFilter() {
        //1.添加cors配置信息
        CorsConfiguration config = new CorsConfiguration();
        //添加允许的跨域信息，前端请求调用信息，不建议用 * ，服务端所有所有请求都可以，产生安全隐患
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://192.168.50.128:8080"); //本地虚拟机tomcat服务器
        //config.addAllowedOrigin("http://center.z.mukewang.com:8080");
        //为以后真实服务域名做准备 用negix去规避
        config.addAllowedOrigin("http://192.168.50.129:90");//本地虚拟机nginx服务器地址
        //config.addAllowedOrigin("http://www.fs.com");//本地虚拟机nginx服务器地址
        config.addAllowedOrigin("http://shop.z.mukewang.com");
        config.addAllowedOrigin("http://center.z.mukewang.com");
        //是否发送cookie信息
        config.setAllowCredentials(true);
        //设置允许请求的方式
        config.addAllowedMethod("*");
        //设置允许的header  前后端交互
        config.addAllowedHeader("*");

        //2.为url添加映射路径
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", config);

        //3.返回重新定义好的corsSource
        return new CorsFilter(corsSource);
    }
}