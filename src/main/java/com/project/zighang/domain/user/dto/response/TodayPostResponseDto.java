package com.project.zighang.domain.user.dto.response;

import com.project.zighang.domain.post.entity.PostEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record TodayPostResponseDto(
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
        List<String> depthTwo,
        Boolean isApplied
) {
    public static TodayPostResponseDto from(PostEntity entity, boolean isApplied) {
        return new TodayPostResponseDto(
                entity.getRecruitmentId(),
                entity.getViewCount(),
                entity.getTitle(),
                entity.getRecruitmentRegion(),
                entity.getMinCareer(),
                entity.getMaxCareer(),
                entity.getRecruitmentEndDate(),
                getCompanyName(entity.getSummaryData()),
                filterValueInSummaryColumn("직무 명", entity.getSummaryData()),
                entity.getRecruitmentOriginalUrl(),
                cleanRawDepthTwoString(entity.getDepthTwo()),
                isApplied
        );
    }

    private static String getCompanyName(String postSummary) {
        String companyName = filterValueInSummaryColumn("회사 명", postSummary);
        if (companyName == null) {
            return filterValueInSummaryColumn("회사명", postSummary);
        }
        return companyName;
    }

    public static TodayPostResponseDto from(PostEntity entity) {
        return from(entity, false);
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

        String[] patterns = {
                "\\*\\*\\s*" + Pattern.quote(value) + "\\s*\\*\\*\\s*:\\s*(.*)",
                "\\*\\*\\s*" + Pattern.quote(value) + "\\s*:\\s*(.*)",
                Pattern.quote(value) + "\\s*:\\s*(.*)"
        };

        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(summary);

            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }

        return null;
    }
}