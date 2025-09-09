package com.project.zighang.global.client.clova;

import com.project.zighang.global.client.clova.dto.ClovaOcrResponse;

public interface OcrReader {
    ClovaOcrResponse extractFromUrl(String imageUrl);

    String extractSuccessTxtFromUrl(String imageUrl);

    String extractTxtFromUrl(String imageUrl);
}
