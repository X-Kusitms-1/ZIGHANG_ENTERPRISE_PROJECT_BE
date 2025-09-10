package com.project.zighang.global.client.azure.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.zighang.global.client.azure.service.AzureReportService;
import com.project.zighang.global.client.azure.dto.AnalysisRequest;
import com.project.zighang.global.exception.Success;
import com.project.zighang.global.exception.template.RspTemplate;
import com.project.zighang.global.prompt.service.PromptBuilder;
import com.project.zighang.global.prompt.service.PromptFinder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/openai")
@RequiredArgsConstructor
public class AiAnalysisController {

    private final AzureReportService azureReportService;
    private final PromptFinder promptFinder;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    @Operation(summary = "채용 공고 분석", description = "합격/불합격 채용 공고를 분석하여 사용자의 강약점을 도출합니다.")
    @ApiResponse(responseCode = "200", description = "분석 결과 반환", content = @Content(mediaType = "application/json"))
    @PostMapping(value = "/report", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public RspTemplate<JsonNode> analyze(@RequestBody @Valid AnalysisRequest req) throws Exception {
        String systemPrompt = promptFinder.findPromptByTag("report");
        String userPrompt = promptBuilder.build(req);

        String raw = azureReportService.generateReport(systemPrompt, userPrompt); // content(JSON 문자열)

        JsonNode jsonNode = objectMapper.readTree(raw);
        return RspTemplate.success(Success.CREATE_REPORT_SUCCESS, jsonNode);
    }
}