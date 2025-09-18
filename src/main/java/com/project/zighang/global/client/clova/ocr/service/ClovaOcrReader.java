package com.project.zighang.global.client.clova.ocr.service;

import com.project.zighang.global.client.clova.ocr.OcrReader;
import com.project.zighang.global.client.clova.ocr.dto.ClovaOcrRequest;
import com.project.zighang.global.client.clova.ocr.dto.ClovaOcrResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClovaOcrReader implements OcrReader {
    private final ClovaOcrClient clovaOcrClient;

    @Override
    public ClovaOcrResponse extractFromUrl(String imageUrl) {
        ClovaOcrRequest req = ClovaOcrRequest.create("V1", "ko", null, imageUrl);
        return clovaOcrClient.extractTextByUrl(req);
    }

    @Override
    public String extractTxtFromUrl(String imageUrl){
        ClovaOcrResponse ocrResponse = extractFromUrl(imageUrl);
        return extractTextWithFilter(ocrResponse, img -> true);
    }

    @Override
    public String extractTextFromPdf(String imageUrl) {
        ClovaOcrResponse ocrResponse = extractFromPdfUrl(imageUrl);
        String result = extractTextWithFilter(ocrResponse, img -> true);
        log.info("OCR SUCCESS");
        return result;
    }

    private ClovaOcrResponse extractFromPdfUrl(String imageUrl) {
        ClovaOcrRequest req = ClovaOcrRequest.createPdf("V1", "ko", null, imageUrl);
        return clovaOcrClient.extractTextByUrl(req);
    }

    @Override
    public String extractSuccessTxtFromUrl(String imageUrl){
        ClovaOcrResponse ocrResponse = extractFromUrl(imageUrl);
        return extractTextWithFilter(ocrResponse, img -> "SUCCESS".equalsIgnoreCase(img.inferResult()));
    }

    private String extractTextWithFilter(ClovaOcrResponse resp, Predicate<ClovaOcrResponse.ImageResult> imageFilter) {
        if (resp == null || resp.images() == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (ClovaOcrResponse.ImageResult img : resp.images()) {
            if (!imageFilter.test(img)) {
                continue;
            }
            List<ClovaOcrResponse.ImageResult.Field> fields = img.fields();
            if (fields == null) {
                continue;
            }
            appendFieldsText(sb, fields);
        }

        return sb.toString().trim();
    }

    private void appendFieldsText(StringBuilder sb, List<ClovaOcrResponse.ImageResult.Field> fields) {
        for (ClovaOcrResponse.ImageResult.Field field : fields) {
            String text = field.inferText();
            if (text == null || text.isBlank()) {
                continue;
            }

            sb.append(text);
            sb.append(Boolean.TRUE.equals(field.lineBreak()) ? '\n' : ' ');
        }
    }
}