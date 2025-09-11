package com.project.zighang.global.client.azure;

import com.project.zighang.global.exception.model.AzureApiException;

public interface ReportGenerator {
    /**
     * JSON 형식으로만 응답을 받는 채팅 완성 요청
     *
     * @param systemPrompt 시스템 프롬프트 (역할/컨텍스트 정의)
     * @param userPrompt   사용자 프롬프트 (실제 요청 내용)
     * @return JSON 형식의 AI 응답 문자열
     * @throws AzureApiException Azure API 호출 실패 시
     */
    String generateReport(String systemPrompt, String userPrompt) throws AzureApiException;
}
