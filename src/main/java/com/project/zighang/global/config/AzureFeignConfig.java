package com.project.zighang.global.config;

import com.project.zighang.domain.subscription.exception.model.AlreadyExistException;
import com.project.zighang.global.exception.Error;
import com.project.zighang.global.exception.model.AzureApiException;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureFeignConfig {

    @Value("${azure.openai.api-key}")
    private String apiKey;

    @Bean
    public RequestInterceptor azureAuthHeaderInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("api-key", apiKey);
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Accept", "application/json");
        };
    }
}