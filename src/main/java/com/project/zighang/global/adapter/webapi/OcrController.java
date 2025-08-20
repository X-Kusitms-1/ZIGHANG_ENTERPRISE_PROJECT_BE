package com.project.zighang.global.adapter.webapi;

import com.project.zighang.global.adapter.webapi.dto.ClovaOcrResponse;
import com.project.zighang.global.presentation.OcrService;
import com.project.zighang.global.presentation.required.OcrReader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {
    private final OcrReader ocrReader;

    @PostMapping("/json")
    public ResponseEntity<ClovaOcrResponse> ocrByUrl(
            @RequestParam String imageUrl,
            @RequestParam(required = false, defaultValue = "ko") String lang,
            @RequestParam(required = false, defaultValue = "V1") String version
    ) {
        ClovaOcrResponse resp = ocrReader.extractFromUrl(imageUrl, lang, version);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/json/text")
    public ResponseEntity<String> ocrTextByUrl(
            @RequestParam String imageUrl,
            @RequestParam(required = false, defaultValue = "ko") String lang,
            @RequestParam(required = false, defaultValue = "V1") String version
    ) {
        String text = ocrReader.extractTxtFromUrl(imageUrl, lang, version);
        return ResponseEntity.ok(text);
    }
}