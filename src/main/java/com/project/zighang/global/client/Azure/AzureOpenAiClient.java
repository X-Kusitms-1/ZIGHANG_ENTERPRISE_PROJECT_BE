package com.project.zighang.global.client.Azure;

import com.project.zighang.global.client.Azure.dto.ChatDTO;
import com.project.zighang.global.config.AzureFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@FeignClient(
        name = "azureOpenAiClient",
        url = "${azure.openai.endpoint}",
        configuration = AzureFeignConfig.class
)
public interface AzureOpenAiClient {
    @PostMapping(
            value = "/openai/deployments/{deployment}/chat/completions?api-version=${azure.openai.api-version}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ChatDTO.ChatResponse createChatCompletion(
            @PathVariable("deployment") String deployment,
            @RequestBody ChatDTO.ChatRequest request
    );
}
