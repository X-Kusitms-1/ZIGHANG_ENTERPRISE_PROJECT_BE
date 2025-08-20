package com.project.zighang.global.presentation;

import com.project.zighang.global.adapter.webapi.dto.ClovaOcrRequest;
import com.project.zighang.global.adapter.webapi.dto.ClovaOcrResponse;
import com.project.zighang.global.presentation.required.OcrReader;
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
    public ClovaOcrResponse extractFromUrl(String imageUrl, String language, String version) {
        String ver = (version == null || version.isBlank()) ? "V1" : version;
        String lang = (language == null || language.isBlank()) ? "ko" : language;

        ClovaOcrRequest req = ClovaOcrRequest.create(ver, lang, null, imageUrl);
        return clovaOcrClient.extractTextByUrl(req);
    }

    @Override
    public String extractTxtFromUrl(String imageUrl, String lang, String version){
        ClovaOcrResponse ocrResponse = extractFromUrl(imageUrl, lang, version);
        return extractTextWithFilter(ocrResponse, img -> true);
    }

    @Override
    public String extractSuccessTxtFromUrl(String imageUrl, String lang, String version){
        ClovaOcrResponse ocrResponse = extractFromUrl(imageUrl, lang, version);
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