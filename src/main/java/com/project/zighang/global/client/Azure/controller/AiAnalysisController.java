package com.project.zighang.global.client.Azure.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.zighang.global.client.Azure.AzureReportService;
import com.project.zighang.global.client.Azure.dto.AnalysisRequest;
import com.project.zighang.global.prompt.service.PromptBuilder;
import com.project.zighang.global.prompt.service.PromptFinder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/openai")
@RequiredArgsConstructor
public class AiAnalysisController {

    private final AzureReportService azure;
    private final PromptFinder promptProvider;
    private final PromptBuilder userPromptBuilder;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "채용 공고 분석",
            description = "합격/불합격 채용 공고를 분석하여 사용자의 강약점을 도출합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "분석 결과 반환",
            content = @Content(mediaType = "application/json")
    )
    @PostMapping(value = "/analysis", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> analyze(@RequestBody @Valid AnalysisRequest req) throws Exception {
        String systemPrompt = promptProvider.findPromptByTag("report");
        String userPrompt = userPromptBuilder.build(req);

        String raw = azure.chatJsonOnly(systemPrompt, userPrompt);
        System.out.println("Azure Raw Response: " + raw);
        JsonNode json = objectMapper.readTree(raw);
        System.out.println("Parsed JSON: " + json.toPrettyString());

        return ResponseEntity.ok(json);
    }
}