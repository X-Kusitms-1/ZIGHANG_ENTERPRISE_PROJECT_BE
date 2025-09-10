package com.project.zighang.global.client.azure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public record AnalysisRequest(
        @NotBlank String promptCode,
        @NotNull List<JobPost> passPosts,
        @NotNull List<JobPost> failPosts
) {
    public record JobPost(
            String id,
            String title,
            String company,
            String body,
            Map<String, Object> meta
    ) {}
}
