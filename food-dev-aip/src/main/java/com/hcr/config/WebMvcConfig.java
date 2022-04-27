package com.hcr.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 实现静态资源的映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //映射文件地址， /** 所有路径
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")  // 映射swagger2
                //#http://localhost:8088/images/2202237F702S6GF8/face-2202237F702S6GF8.png
                .addResourceLocations("file:H:/videos/workspaces/images");  // 映射本地静态资源
    }

    //构建一个bean让容器扫描到
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
