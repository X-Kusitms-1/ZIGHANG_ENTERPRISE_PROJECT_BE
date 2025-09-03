package com.project.zighang.post.dto;

import com.project.zighang.post.entity.PostEntity;

public record PostResponseDto(
        Long recruitmentId, // pk
        String title,   // 공고명
        String recruitmentRegion,   // 지역
        Integer minCareer,  // 최소 경력
        Integer maxCareer,  // 최대 경력
        String recruitmentEndDate,
        String summaryData,
        String recruitmentOriginUrl,
        String depthTwo
) {
    public static PostResponseDto from(PostEntity entity) {
        return new PostResponseDto(
                entity.getRecruitmentId(),
                entity.getTitle(),
                entity.getRecruitmentRegion(),
                entity.getMinCareer(),
                entity.getMaxCareer(),
                entity.getRecruitmentEndDate(),
                entity.getSummaryData(),
                entity.getRecruitmentOriginalUrl(),
                entity.getDepthTwo()
        );
    }
}