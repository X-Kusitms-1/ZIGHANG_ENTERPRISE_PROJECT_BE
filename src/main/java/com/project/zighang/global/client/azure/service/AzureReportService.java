package com.project.zighang.global.client.azure.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.zighang.global.exception.Error;
import com.project.zighang.global.client.azure.ReportGenerator;
import com.project.zighang.global.exception.model.AzureApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AzureReportService implements ReportGenerator {

    private final AzureOpenAiClient client;
    private final ObjectMapper objectMapper;

    @Value("${azure.openai.deployment}")
    private String deployment;

    @Value("${azure.openai.api-version}")
    private String apiVersion;

    @Value("${azure.openai.max-completion-tokens:4096}")
    private Integer maxCompletionTokens;

    @Override
    public String generateReport(String systemPrompt, String userPrompt) {
        try {
            Map<String, Object> body = Map.of(
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt == null ? "" : systemPrompt),
                            Map.of("role", "user",   "content", userPrompt   == null ? "" : userPrompt)
                    ),
                    "response_format", Map.of("type", "json_object"),
                    "max_completion_tokens", maxCompletionTokens
            );

            String jsonBody = objectMapper.writeValueAsString(body);

            String raw = client.createChatCompletionRaw(deployment, apiVersion, jsonBody);

            JsonNode root = objectMapper.readTree(raw);
            JsonNode message = root.path("choices").path(0).path("message");
            String content = extractAssistantContent(message);

            if (content == null || content.isBlank()) {
                throw new AzureApiException(Error.CONTENT_NOT_FOUND, Error.CONTENT_NOT_FOUND.getMessage());
            }

            return content;
        } catch (Exception e) {
            throw new AzureApiException(Error.AZURE_API_ERROR, "Azure API 호출 실패: " + e.getMessage());
        }
    }

    private String extractAssistantContent(JsonNode message) {
        JsonNode jsonNode = message.path("content");
        if (jsonNode.isTextual()) {
            return jsonNode.asText();
        }
        if (jsonNode.isArray()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (JsonNode item : jsonNode) {
                String t = item.path("text").asText(null);
                if (t != null) stringBuilder.append(t);
            }
            if (stringBuilder.length() > 0) return stringBuilder.toString();
        }
        return null;
    }
}