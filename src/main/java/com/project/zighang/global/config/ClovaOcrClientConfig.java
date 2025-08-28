package com.project.zighang.global.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClovaOcrClientConfig {
    @Value("${clova.ocr.api-key}")
    private String ocrSecret;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header("X-OCR-SECRET", ocrSecret);
    }
}