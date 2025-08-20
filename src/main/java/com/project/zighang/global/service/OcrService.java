package com.project.zighang.global.service;

import com.project.zighang.global.dto.ClovaOcrRequest;
import com.project.zighang.global.dto.ClovaOcrResponse;
import com.project.zighang.global.util.ClovaOcrClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class OcrService implements OcrReader {
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