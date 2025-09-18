package com.project.zighang.global.client.clova.embedding.service;

import com.project.zighang.global.client.clova.embedding.EmbeddingGenerator;
import com.project.zighang.global.client.clova.embedding.dto.EmbeddingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EmbeddingService implements EmbeddingGenerator {

    private final RestClient ncpEmbeddingClient;

    public EmbeddingService(@Qualifier("NcpEmbeddingClient") RestClient ncpEmbeddingClient) {
        this.ncpEmbeddingClient = ncpEmbeddingClient;
    }

    @Value("${cloud.ncp.embedding.endpoint}")
    private String embeddingEndpoint;

    @Override
    public List<Double> embed(String text) {
        String requestId = UUID.randomUUID().toString().replace("-", "");

        EmbeddingDto.EmbeddingV2Response res = ncpEmbeddingClient.post()
                .uri(embeddingEndpoint)
                .header("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
                .body(new EmbeddingDto.EmbeddingV2Request(text))
                .retrieve()
                .body(EmbeddingDto.EmbeddingV2Response.class);

        log.info("Embedding response: {}", res);
        if (res == null || res.result() == null || res.result().embedding() == null) {
            throw new IllegalStateException("Empty embedding result");
        }
        return res.result().embedding();
    }
}
