package com.project.zighang.global.client.Azure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


public class ChatDTO {


    public record Message(String role, String content) {}


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ResponseFormat(String type) {}


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatRequest(
            List<Message> messages,
            @JsonProperty("max_completion_tokens") Integer maxCompletionTokens,
            @JsonProperty("response_format") ResponseFormat responseFormat
    ) {}


    public record ChatResponse(List<Choice> choices) {
        public String firstContent() {
            if (choices == null || choices.isEmpty() || choices.get(0).message() == null) return null;
            return choices.get(0).message().content();
        }
    }


    public record Choice(Message message) {}
}
