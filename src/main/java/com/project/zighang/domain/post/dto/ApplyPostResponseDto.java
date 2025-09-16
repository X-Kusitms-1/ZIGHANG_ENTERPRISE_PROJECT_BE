package com.project.zighang.domain.post.dto;

import com.project.zighang.domain.post.entity.PostApplyEntity;
import com.project.zighang.domain.post.entity.PostEntity;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record ApplyPostResponseDto(
        Long recruitmentId,
        String companyName,
        String recruitmentOriginUrl,
        List<String> depthTwo,
        String applyStatus,
        String createdAt
) {
    public static ApplyPostResponseDto from(PostApplyEntity entity) {
        PostEntity postEntity = entity.getPostEntity();
        String applyStatus = "대기중";

        if (entity.getApplyStatus() != null && entity.getApplyStatus().getDescription() != null) {
            applyStatus = entity.getApplyStatus().getDescription();
        }

        return new ApplyPostResponseDto(
                postEntity.getRecruitmentId(),
                filterValueInSummaryColumn("회사 명", postEntity.getSummaryData()),
                filterValueInSummaryColumn("직무 명", postEntity.getSummaryData()),
                cleanRawDepthTwoString(postEntity.getDepthTwo()),
                applyStatus,
                entity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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
