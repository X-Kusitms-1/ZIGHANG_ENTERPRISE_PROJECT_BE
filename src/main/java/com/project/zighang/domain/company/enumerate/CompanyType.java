package com.project.zighang.domain.company.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanyType {
    LARGE_ENTERPRISE("대기업"),
    MID_SIZED_ENTERPRISE("중견기업"),
    SMALL_MEDIUM_ENTERPRISE("중소기업"),
    STARTUP("스타트업"),
    UNICORN("유니콘"),
    FOREIGN_COMPANY("외국계기업"),
    PUBLIC_OR_STATE_OWNED("공공기관/공기업"),
    FINANCIAL_SERVICES("금융권"),
    OTHER("기타");

    private final String description;
}
