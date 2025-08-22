// src/main/java/com/eiummarket/demo/config/WebConfig.java
package com.eiummarket.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.public-base:/uploads}")
    private String publicBase;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** -> 로컬 디렉터리 매핑
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        String location = "file:" + dir.toString() + "/";
        String pattern = publicBase.endsWith("/") ? publicBase + "**" : publicBase + "/**";
        registry.addResourceHandler(pattern).addResourceLocations(location);
    }
}
