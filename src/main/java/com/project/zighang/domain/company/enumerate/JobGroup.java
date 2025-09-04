package com.project.zighang.domain.company.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobGroup {
    SOFTWARE_ENGINEERING("개발"),
    DATA_AI("데이터/AI"),
    DESIGN("디자인"),
    PRODUCT_MANAGEMENT("기획/PM"),
    MARKETING_ADS_PR("마케팅/광고/PR"),
    SALES_BUSINESS("영업/세일즈"),
    BUSINESS_ADMIN_HR("경영/HR/사무"),
    CUSTOMER_SUCCESS_SUPPORT("CS/고객지원"),
    MANUFACTURING_QA_PROCUREMENT("생산/품질/구매"),
    LOGISTICS_TRANSPORT("물류/운송"),
    RESEARCH_RND("연구/R&D"),
    EDUCATION("교육"),
    MEDICAL_BIO("의료/바이오"),
    OTHER_PROFESSIONALS("기타 전문직");

    private final String description;
}