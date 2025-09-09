package com.project.zighang.domain.company.dto;

public record CompanyThumbnail(
        Long id,

        String companyNameKr,

        String companyThumbnailUrl,

        String companyTypeLabel,

        boolean isSubscribed
) {}