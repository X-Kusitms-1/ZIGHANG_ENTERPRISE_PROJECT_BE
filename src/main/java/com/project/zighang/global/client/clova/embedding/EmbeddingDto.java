package com.project.zighang.global.client.clova.embedding;

import java.util.List;

public record EmbeddingDto () {

    public record EmbeddingV2Request(
            String text
    ) {}

    public record EmbeddingV2Result(
            List<Double> embedding,
            Integer numTokens
    ) {}

    public record EmbeddingV2Response(
            EmbeddingV2Result result
    ) {}
}
