package com.project.zighang.domain.post.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ApplyStatus {
    PASSED("passed", "합격"),
    PENDING("pending", "심사중"),
    REJECTED("rejected", "탈락");

    private final String code;
    private final String description;

    public static ApplyStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(status -> status.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Invalid status code: %s. Valid codes: %s",
                                code, Arrays.toString(getAllCodes()))));
    }

    private static String[] getAllCodes() {
        return Arrays.stream(values())
                .map(ApplyStatus::getCode)
                .toArray(String[]::new);
    }
}

