package com.project.zighang.global.prompt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.zighang.global.client.Azure.dto.AnalysisRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PromptBuilder {
    private final ObjectMapper objectMapper;

    public String build(AnalysisRequest req) {
        try {
            String passJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(req.passPosts());
            String failJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(req.failPosts());

            return """
                다음은 한 사용자에 대한 지원 이력입니다.
                - 목표: 이 사용자가 어떤 공고에 강하고/약한지 정량·정성 분석
                - 주의: 직무가 서로 달라도 클러스터링으로 분리한 뒤 비교

                [합격 공고 목록]
                """ + passJson + """
                [불합격 공고 목록] 
                """ + failJson + """
                출력은 반드시 지정 JSON 스키마로만 반환해 주세요.
                """;

        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to build user prompt", e);
        }
    }
}
