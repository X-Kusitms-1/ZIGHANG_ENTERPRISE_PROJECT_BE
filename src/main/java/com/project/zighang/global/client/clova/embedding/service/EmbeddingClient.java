package com.project.zighang.global.client.clova.embedding.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class EmbeddingClient {

    @Value("${cloud.ncp.embedding.api-key}")
    private String apiKey;

    @Value("${cloud.ncp.embedding.baseurl}")
    private String baseUrl;

    @Bean
    public RestClient NcpEmbeddingClient(RestClient.Builder builder) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
