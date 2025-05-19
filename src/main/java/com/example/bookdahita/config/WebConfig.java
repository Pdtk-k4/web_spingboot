package com.example.bookdahita.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/uploads/**")
                .addResourceLocations("file:src/main/resources/static/resources/uploads/")
                .setCachePeriod(0); // Tắt cache
    }
}