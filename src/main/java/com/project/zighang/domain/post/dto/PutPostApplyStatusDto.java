package com.project.zighang.domain.post.dto;

import com.project.zighang.domain.post.enumerate.ApplyStatus;

public record PutPostApplyStatusDto(
        Long recruitmentId,
        String statusCode
) {
    public ApplyStatus getApplyStatus() {
        return ApplyStatus.fromCode(statusCode);
    }
}