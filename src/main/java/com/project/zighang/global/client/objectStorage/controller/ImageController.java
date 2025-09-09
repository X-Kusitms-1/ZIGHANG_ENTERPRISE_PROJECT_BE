package com.project.zighang.global.client.objectStorage.controller;

import com.project.zighang.global.client.objectStorage.dto.PreSignedUrlResponse;
import com.project.zighang.global.client.objectStorage.PresignedUrlReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/image")
@RequiredArgsConstructor
public class ImageController {

    private final PresignedUrlReader presignedUrlReader;

    @GetMapping("/presigned-url")
    public PreSignedUrlResponse presignedUrl(@RequestParam String prefix, @RequestParam String fileName) {
        return presignedUrlReader.getPreSignedUrl(prefix, fileName);
    }
}