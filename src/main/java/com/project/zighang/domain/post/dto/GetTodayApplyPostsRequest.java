package com.project.zighang.domain.post.dto;

import jakarta.validation.constraints.NotNull;

public record GetTodayApplyPostsRequest(
        @NotNull Boolean isFirstApiCall,
        @NotNull Long requireRefreshRecruitmentId
) {}
