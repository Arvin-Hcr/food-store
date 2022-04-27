package com.hcr.resource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 属性资源文件与类映射
 */
//组件让springboot扫描
@Component
//属性资源文件配置 前缀
@ConfigurationProperties(prefix = "file")
//项目打包resource文件都在class path下，切换环境发布项目只需将prod更改即可
@PropertySource("classpath:file-upload-prod.properties")
public class FileUpload {

    private String imageUserFaceLocation;
    private String imageServerUrl;

    public String getImageUserFaceLocation() {
        return imageUserFaceLocation;
    }

    public void setImageUserFaceLocation(String imageUserFaceLocation) {
        this.imageUserFaceLocation = imageUserFaceLocation;
    }

    public String getImageServerUrl() {
        return imageServerUrl;
    }

    public void setImageServerUrl(String imageServerUrl) {
        this.imageServerUrl = imageServerUrl;
    }
}
