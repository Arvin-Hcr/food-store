package com.hcr.config;

import com.hcr.interceptor.UserTokenInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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
                //http://localhost:8088/images/220427FN4N3A0H94/face-220427FN4N3A0H9420220427203518.png
                //.addResourceLocations("file:H:\\\\videos\\\\workspaces\\\\images");  // 映射本地静态资源 映射该目录下的所有文件
                .addResourceLocations("file:/workspaces/images/");  //生产环境
    }

    //构建一个bean让容器扫描到
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor(){
        return new UserTokenInterceptor();
    }

    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTokenInterceptor()).addPathPatterns("/hello");  //路由地址
        //完成注册
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
