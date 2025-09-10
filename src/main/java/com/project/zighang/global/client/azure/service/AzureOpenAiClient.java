package com.project.zighang.global.client.azure.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "azureOpenAiClient",
        url = "${azure.openai.endpoint}",
        configuration = com.project.zighang.global.config.AzureFeignConfig.class
)
public interface AzureOpenAiClient {

    @PostMapping(
            value = "/openai/deployments/{deployment}/chat/completions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    String createChatCompletionRaw(
            @PathVariable("deployment") String deployment,
            @RequestParam("api-version") String apiVersion,
            @RequestBody String body
    );
}
