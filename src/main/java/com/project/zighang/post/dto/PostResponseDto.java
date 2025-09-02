package com.project.zighang.post.dto;

import com.project.zighang.post.entity.PostEntity;

public record PostResponseDto(
        Long recruitmentId,
        String title,
        Integer score,
        String affiliate,
        String recruitmentRegion,
        String recruitmentImageUrl,
        Integer viewCount,
        Integer minCareer,
        Integer maxCareer,
        String industry,
        String recruitmentEndDate
) {
    public static PostResponseDto from(PostEntity entity) {
        return new PostResponseDto(
                entity.getRecruitmentId(),
                entity.getTitle(),
                entity.getScore(),
                entity.getAffiliate(),
                entity.getRecruitmentRegion(),
                entity.getRecruitmentImageUrl(),
                entity.getViewCount(),
                entity.getMinCareer(),
                entity.getMaxCareer(),
                entity.getIndustry(),
                entity.getRecruitmentEndDate()
        );
    }
}