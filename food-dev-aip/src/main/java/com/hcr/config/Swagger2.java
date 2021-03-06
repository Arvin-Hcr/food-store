package com.hcr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class Swagger2 {
    //原路径
    //http://localhost:8088/swagger-ui.html
    //http://localhost:8088/doc.html
    //swagger2核心配置 docket
    @Bean
    public Docket createRestApi(){

        return new Docket(DocumentationType.SWAGGER_2)    //指定api类型为swagger2
                .apiInfo(apiInfo())                               //用于定义api文档汇总信息
                .select().apis(RequestHandlerSelectors.basePackage("com.hcr.controller"))           //指定controller包
                .paths(PathSelectors.any())                       //所有controller
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("天天吃货 电商平台接口api")      //文档页标题
                .contact(new Contact("imooc",
                        "https://www.baidu.com",
                        "h_chaoran@163.com"))   //联系人信息
                .description("专为天天吃货提供的api文档")        //详细信息
                .version("1.0.1")                              //版本号
                .termsOfServiceUrl("https://www.baidu.com")           //网站地址
                .build();                                      //构建,将apiInfo返回出去
    }
}
