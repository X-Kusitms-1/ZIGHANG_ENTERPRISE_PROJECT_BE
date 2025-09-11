package com.project.zighang.global.client.clova.dto;

import java.util.List;
import java.util.UUID;

public record ClovaOcrRequest(
        String version,
        String requestId,
        long timestamp,
        String lang,
        List<Image> images
) {
    public static ClovaOcrRequest create(String version, String lang, List<Image> images) {
        return new ClovaOcrRequest(
                version,
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                lang,
                images
        );
    }

    public static ClovaOcrRequest create(String version, String lang, String resultType, String imageUrl) {
        return create(version, lang, List.of(Image.create("png", "input", imageUrl)));
    }

    public record Image(
            String format,
            String name,
            String url
    ) {
        public static Image create(String format, String name, String url) {
            return new Image(format, name, url);
        }
    }
}