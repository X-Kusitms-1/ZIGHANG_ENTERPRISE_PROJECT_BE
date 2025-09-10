package com.project.zighang.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record IndustryDto(
        @Schema(description = "직군", example = "IT·개발") @NotBlank String jobFamily,
        @Schema(description = "직무", example = "백엔드") @NotBlank String role
) {
}
