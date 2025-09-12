package com.project.zighang.domain.post.dto;

import com.project.zighang.domain.post.entity.ResumeFileEntity;

import java.time.LocalDateTime;

public record ResumeFileResponseDto(
        Long id,
        String originalFileName,
        String objectUrl,
        LocalDateTime uploadedAt
) {
    public static ResumeFileResponseDto from(ResumeFileEntity entity) {
        return new ResumeFileResponseDto(
                entity.getId(),
                entity.getOriginalFileName(),
                entity.getObjectUrl(),
                entity.getCreatedAt()
        );
    }
}