package com.project.zighang.domain.post.dto;

public record ApplyCountDto(
        Integer todayApplyCount,
        Integer thisWeekApplyCount,
        Integer totalApplyCount
) {
}
