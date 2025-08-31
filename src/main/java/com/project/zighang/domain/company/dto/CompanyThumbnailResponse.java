package com.project.zighang.domain.company.dto;

public record CompanyThumbnailResponse(
        Long id,

        String companyNameKr,

        String companyThumbnailUrl,

        String companyTypeLabel // enum description
) {}