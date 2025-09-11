package com.project.zighang.domain.post.dto;

import com.project.zighang.domain.post.entity.PostEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record PostResponseDto(
        Long recruitmentId,
        Integer viewCount,
        String title,
        String recruitmentRegion,
        Integer minCareer,
        Integer maxCareer,
        String recruitmentEndDate,
        String companyName,
        String workSummary,
        String recruitmentOriginUrl,
        List<String> depthTwo
) {
    public static PostResponseDto from(PostEntity entity) {
        return new PostResponseDto(
                entity.getRecruitmentId(),
                entity.getViewCount(),
                entity.getTitle(),
                entity.getRecruitmentRegion(),
                entity.getMinCareer(),
                entity.getMaxCareer(),
                entity.getRecruitmentEndDate(),
                filterValueInSummaryColumn("회사 명", entity.getSummaryData()),
                filterValueInSummaryColumn("직무 명", entity.getSummaryData()),
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

    private static String filterValueInSummaryColumn(String value, String summary) {
        if (summary == null || summary.isBlank()) {
            return null;
        }

        Pattern pattern = Pattern.compile("\\*\\*" + Pattern.quote(value) + "\\*\\*\\s*:\\s*(.*)");
        Matcher matcher = pattern.matcher(summary);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null;
    }
}