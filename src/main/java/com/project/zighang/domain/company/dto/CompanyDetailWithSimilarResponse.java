package com.project.zighang.domain.company.dto;

import java.util.List;

public record CompanyDetailWithSimilarResponse(
        CompanyThumbnail company,

        List<CompanyNews> newAll,                  // 이 회사의 모든 뉴스 (요구사항대로 전체)

        List<CompanyWithNewsResponse> similarCompanies   // 최대 3개, 각 3뉴스
) {}