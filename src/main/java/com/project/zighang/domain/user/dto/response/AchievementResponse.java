package com.project.zighang.domain.user.dto.response;

import lombok.Builder;

@Builder
public record AchievementResponse(
        Long goalCount,
        Integer currentCount,
        Integer percentage
) {
    public static AchievementResponse of(Integer currentCount, Long goalCount) {
        int percentage = calculatePercentage(currentCount, goalCount);
        return AchievementResponse.builder()
                .currentCount(currentCount)
                .goalCount(goalCount)
                .percentage(percentage)
                .build();
    }

    private static int calculatePercentage(Integer currentCount, Long goalCount) {
        if (goalCount == null || goalCount == 0) {
            return 0;
        }

        if (currentCount == null || currentCount == 0) {
            return 0;
        }

        double percentage = ((double) currentCount / goalCount) * 100;
        return (int) Math.round(percentage);
    }
}
