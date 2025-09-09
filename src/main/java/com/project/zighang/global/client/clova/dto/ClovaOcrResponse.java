package com.project.zighang.global.client.clova.dto;

import java.util.List;

public record ClovaOcrResponse(
        String version,
        String requestId,
        long timestamp,
        List<ImageResult> images
) {
    public record ImageResult(
            String uid,
            String name,
            String inferResult,
            String message,
            List<Field> fields
    ) {
        public record Field(
                String valueType,
                String inferText,
                Double inferConfidence,
                String type,
                Boolean lineBreak
        ) {}
    }
}
