package com.project.zighang.global.prompt.controller;

import com.project.zighang.global.exception.Success;
import com.project.zighang.global.exception.template.RspTemplate;
import com.project.zighang.global.prompt.service.PromptBuilder;
import com.project.zighang.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/openai2")
@RequiredArgsConstructor
public class PromptController {

    private final PromptBuilder promptBuilder;

    @Operation(summary = "채용 공고 분석", description = "합격/불합격 채용 공고를 분석하여 사용자의 강약점을 도출합니다.")
    @ApiResponse(responseCode = "200", description = "분석 결과 반환", content = @Content(mediaType = "application/json"))
    @PostMapping(value = "/report")
    public RspTemplate<?> analyze(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        promptBuilder.buildReportRequest(userDetails.getUserEntity());
        return RspTemplate.success(Success.CREATE_REPORT_SUCCESS);
    }
}
