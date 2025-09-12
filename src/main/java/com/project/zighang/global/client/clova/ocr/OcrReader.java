package com.project.zighang.global.client.clova.ocr;

import com.project.zighang.global.client.clova.ocr.dto.ClovaOcrResponse;

public interface OcrReader {
    ClovaOcrResponse extractFromUrl(String imageUrl);

    String extractSuccessTxtFromUrl(String imageUrl);

    String extractTxtFromUrl(String imageUrl);
}
