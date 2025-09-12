package com.project.zighang.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UploadCompleteRequestDto(
        @NotBlank(message = "원본 파일명은 필수입니다")
        String originalFileName,

        @NotBlank(message = "Object URL은 필수입니다")
        String objectUrl,

        @NotNull(message = "채용공고 ID는 필수입니다")
        Long recruitmentId
) {
}
