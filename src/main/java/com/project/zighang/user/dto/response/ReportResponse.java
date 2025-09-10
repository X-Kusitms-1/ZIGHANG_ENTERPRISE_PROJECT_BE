package com.project.zighang.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ReportResponse() {

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
