package com.project.zighang.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.zighang.user.entity.WeeklyReport;

import java.time.LocalDate;
import java.util.List;

public record ReportResponse() {

    public record Weekly(
            Integer weekNumber,
            Integer year,
            Integer month,
            Integer weekOfMonth,
            String formattedWeek,
            Integer passedCount,
            Integer rejectedCount,
            LocalDate startDate,
            LocalDate endDate,
            ReportDataDto reportData
    ) {
        public static Weekly from(WeeklyReport report, ReportDataDto reportData) {
            return new Weekly(
                    report.getWeekNumber(),
                    report.getYear(),
                    report.getMonth(),
                    report.getWeekOfMonth(),
                    report.getFormattedWeek(),
                    report.getPassedCount(),
                    report.getRejectedCount(),
                    report.getStartDate(),
                    report.getEndDate(),
                    reportData
            );
        }
    }

    public record ReportDataDto(
            @JsonProperty("passed_features")
            List<String> passedFeatures,

            @JsonProperty("failed_features")
            List<String> failedFeatures,

            List<RecommendationDto> recommendations
    ) {}

    public record RecommendationDto(
            String category,
            String requirement
    ) {}
}