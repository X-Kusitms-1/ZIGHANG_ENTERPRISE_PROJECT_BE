package com.project.zighang.global.client.opensearch.service;

import com.project.zighang.global.client.opensearch.PostRecommendsFinder;
import com.project.zighang.global.client.opensearch.dto.OpenSearchDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class OpenSearchQueryService implements PostRecommendsFinder {

    private final RestClient OpClient;

    public OpenSearchQueryService(@Qualifier("OpClient") RestClient OpClient) {
        this.OpClient = OpClient;
    }

    @Value("${opensearch.index:ilhaeng-embeddings}")
    private String index;

    @Value("${opensearch.vector-field:jobReasoning}")
    private String vfield;

    @Override
    public OpenSearchDto.SearchResponse<OpenSearchDto.JobReasoningSource> knnRaw(OpenSearchDto.KnnReq knnRequest) {

        Map<String, Object> body = Map.of(
                "size", knnRequest.k(),
                "query", Map.of("knn", Map.of(
                        vfield, Map.of("vector", knnRequest.vector(), "k", knnRequest.k())
                ))
        );

        return OpClient.post()
                .uri("/{index}/_search", index)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<OpenSearchDto.SearchResponse<OpenSearchDto.JobReasoningSource>>() {});
    }

    @Override
    public OpenSearchDto.KnnViewResponse knn(OpenSearchDto.KnnReq knnRequest) {
        OpenSearchDto.SearchResponse<OpenSearchDto.JobReasoningSource> response = knnRaw(knnRequest);
        return OpenSearchDto.KnnViewResponse.from(response);
    }
}
