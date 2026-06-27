package com.example.secondhand.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 让浏览器可以访问上传后的商品图片：/uploads/product/xxx.jpg
        String[] locations = new String[UploadPaths.uploadRoots().size()];
        int index = 0;
        for (Path root : UploadPaths.uploadRoots()) {
            String location = root.toUri().toString();
            if (!location.endsWith("/")) {
                location += "/";
            }
            locations[index++] = location;
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(locations);
    }
}
