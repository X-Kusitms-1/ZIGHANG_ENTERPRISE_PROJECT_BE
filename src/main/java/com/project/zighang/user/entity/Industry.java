package com.project.zighang.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Industry {

    IT_DEVELOPMENT("IT·개발"),
    AI_DATA("AI·데이터"),
    GAME("게임"),
    DESIGN("디자인"),
    PLANNING_STRATEGY("기획·전략"),

    MARKETING_ADVERTISING("마케팅·광고"),
    MERCHANDISING("상품기획·MD"),
    SALES("영업"),
    TRADE_LOGISTICS("무역·물류"),
    TRANSPORTATION_DELIVERY("운송·배송"),

    LEGAL_AFFAIRS("법률·법무"),
    HR_GENERAL_AFFAIRS("HR·총무"),
    ACCOUNTING_FINANCE_TAX("회계·재무·세무"),
    SECURITIES_ASSET_MANAGEMENT("증권·운용"),
    BANKING_CARD_INSURANCE("은행·카드·보험"),

    ENGINEERING_RD("엔지니어링·R&D"),
    CONSTRUCTION_ARCHITECTURE("건설·건축"),
    PRODUCTION_SKILLED_LABOR("생산·기능직"),
    MEDICAL_HEALTHCARE("의료·보건"),
    PUBLIC_SECTOR_WELFARE("공공·복지"),

    EDUCATION("교육"),
    MEDIA_ENTERTAINMENT("미디어·엔터"),
    CUSTOMER_SERVICE_TM("고객상담·TM"),
    SERVICE("서비스"),
    FOOD_BEVERAGE("식음료");

    private final String title;
}
