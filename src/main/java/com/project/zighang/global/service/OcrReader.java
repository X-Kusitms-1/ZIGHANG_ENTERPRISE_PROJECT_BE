package com.project.zighang.global.service;

import com.project.zighang.global.dto.ClovaOcrResponse;

public interface OcrReader {
    ClovaOcrResponse extractFromUrl(String imageUrl);

    String extractSuccessTxtFromUrl(String imageUrl);

    String extractTxtFromUrl(String imageUrl);
}
