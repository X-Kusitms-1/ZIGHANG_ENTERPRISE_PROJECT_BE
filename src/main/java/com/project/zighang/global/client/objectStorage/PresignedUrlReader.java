package com.project.zighang.global.client.objectStorage;

import com.project.zighang.global.client.objectStorage.dto.PreSignedUrlResponse;

public interface PresignedUrlReader {
    PreSignedUrlResponse getPreSignedUrl(String prefix, String originalFileName);
}
