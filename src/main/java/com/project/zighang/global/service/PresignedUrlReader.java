package com.project.zighang.global.service;

import com.project.zighang.global.dto.PreSignedUrlResponse;

public interface PresignedUrlReader {
    PreSignedUrlResponse getPreSignedUrl(String prefix, String originalFileName);
}
