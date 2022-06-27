package com.hcr;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 打war包首先clean 之后install直接会在 food-dev-api 的target中生成，然后将war上传服务器即可
 */
// 打包war [4] 增加war的启动类
public class WarStarterApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 指向ApplicationShop这个springboot启动类
        return builder.sources(ApplicationShop.class);
    }
}
