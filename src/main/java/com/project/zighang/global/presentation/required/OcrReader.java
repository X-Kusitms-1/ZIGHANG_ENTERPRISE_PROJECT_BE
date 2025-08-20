package com.project.zighang.global.presentation.required;

import com.project.zighang.global.adapter.webapi.dto.ClovaOcrResponse;

public interface OcrReader {
    ClovaOcrResponse extractFromUrl(String imageUrl, String lang, String version);

    String extractSuccessTxtFromUrl(String imageUrl, String lang, String version);

    String extractTxtFromUrl(String imageUrl, String lang, String version);
}
