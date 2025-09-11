package com.project.zighang.domain.post.dto;

import com.project.zighang.global.client.objectStorage.dto.PreSignedUrlResponse;

public record ResumeResponse(
        PreSignedUrlResponse preSignedUrlResponse
) {
}
