package com.project.zighang.domain.post.dto;

import jakarta.validation.constraints.NotNull;

public record DeleteApplyJobDto(
        @NotNull Long recruitmentId
) {
}
