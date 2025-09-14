package com.project.zighang.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostTodayApplyPostsRequest {

    @Schema(description = "공고 ID 목록", example = "[1, 2, 3]")
    @NotEmpty(message = "공고 ID 목록은 비어있을 수 없습니다.")
    private List<Long> recruitmentIdList;
}