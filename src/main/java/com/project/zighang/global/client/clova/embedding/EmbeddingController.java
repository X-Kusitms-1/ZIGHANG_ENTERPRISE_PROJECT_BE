package com.project.zighang.global.client.clova.embedding;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/embedding")
public class EmbeddingController {
    private final EmbeddingService embeddingService;

    @PostMapping
    public ResponseEntity<?> embed(@Valid @RequestBody EmbeddingDto.EmbeddingV2Request req) {
        var result = embeddingService.embed(req.text());

        return ResponseEntity.ok(result);
    }
}
