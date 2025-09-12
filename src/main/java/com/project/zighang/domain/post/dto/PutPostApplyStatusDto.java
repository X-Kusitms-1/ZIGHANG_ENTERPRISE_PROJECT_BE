package com.project.zighang.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PutPostApplyStatusDto(
        @Schema(description = "공고 ID", example = "1")
        @NotNull(message = "공고 ID는 필수입니다")
        Long recruitmentId,

        @Schema(description = "지원 상태 코드",
                allowableValues = {"pending", "passed", "rejected"},
                example = "pending")
        @NotBlank(message = "상태 코드는 필수입니다")
        String statusCode
) {
}