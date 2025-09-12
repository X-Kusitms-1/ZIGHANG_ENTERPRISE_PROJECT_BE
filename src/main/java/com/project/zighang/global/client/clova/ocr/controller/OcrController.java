package com.project.zighang.global.client.clova.ocr.controller;

import com.project.zighang.global.client.clova.ocr.dto.ClovaOcrResponse;
import com.project.zighang.global.client.clova.ocr.OcrReader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {
    private final OcrReader ocrReader;

    @PostMapping("/json")
    public ResponseEntity<ClovaOcrResponse> ocrByUrl(@RequestParam String imageUrl) {
        ClovaOcrResponse resp = ocrReader.extractFromUrl(imageUrl);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/json/text")
    public ResponseEntity<String> ocrTextByUrl(@RequestParam String imageUrl) {
        String text = ocrReader.extractTxtFromUrl(imageUrl);
        return ResponseEntity.ok(text);
    }
}