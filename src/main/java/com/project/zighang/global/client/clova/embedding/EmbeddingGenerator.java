package com.project.zighang.global.client.clova.embedding;

import java.util.List;

public interface EmbeddingGenerator {
    List<Double> embed(String text);
}
