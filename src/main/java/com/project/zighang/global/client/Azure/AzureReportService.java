package com.project.zighang.global.client.Azure;

import com.project.zighang.global.client.Azure.dto.ChatDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
@RequiredArgsConstructor
public class AzureReportService {

    private final AzureOpenAiClient client;

    @Value("${azure.openai.deployment}")
    private String deployment;

    @Value("${azure.openai.max-completion-tokens:2048}")
    private Integer maxCompletionTokens;


    public String chatJsonOnly(String systemPrompt, String userPrompt) {
        var req = new ChatDTO.ChatRequest(
                List.of(
                        new ChatDTO.Message("system", systemPrompt),
                        new ChatDTO.Message("user", userPrompt)
                ),
                maxCompletionTokens,
                new ChatDTO.ResponseFormat("json_object")
        );

        var resp = client.createChatCompletion(deployment, req);

        System.out.println("Azure Response: " + resp);
        var content = resp != null ? resp.firstContent() : null;
        if (content == null) throw new IllegalStateException("Empty Azure response");
        return content;
    }
}
