package com.project.zighang.domain.post.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplyStatus {
    PASSED("합격"),
    PENDING("심사중"),
    REJECTED("탈락");

    private final String description;
}

