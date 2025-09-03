package com.project.zighang.post.dto;

import com.project.zighang.post.entity.PostEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record PostResponseDto(
        Long recruitmentId, // pk
        String title,   // 공고명
        String recruitmentRegion,   // 지역
        Integer minCareer,  // 최소 경력
        Integer maxCareer,  // 최대 경력
        String recruitmentEndDate,
//        String summaryData,
        String recruitmentOriginUrl,
        List<String> depthTwo
) {
    public static PostResponseDto from(PostEntity entity) {
        return new PostResponseDto(
                entity.getRecruitmentId(),
                entity.getTitle(),
                entity.getRecruitmentRegion(),
                entity.getMinCareer(),
                entity.getMaxCareer(),
                entity.getRecruitmentEndDate(),
//                entity.getSummaryData(),
                entity.getRecruitmentOriginalUrl(),
                cleanRawDepthTwoString(entity.getDepthTwo())
        );
    }

    private static List<String> cleanRawDepthTwoString(String data) {
        if (data == null || data.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(data.split(","))
                .map(s -> s.trim().replace("'", ""))
                .collect(Collectors.toList());
    }
}