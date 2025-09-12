package com.project.zighang.global.client.clova.embedding;

import java.util.List;

public interface EmbeddingGenerator {
    /**
     * 주어진 텍스트를 임베딩 벡터로 변환합니다.
     *
     * @param text 변환할 텍스트
     * @return 임베딩 벡터 (List<Double> 형태)
     */
    List<Double> embed(String text);
}
