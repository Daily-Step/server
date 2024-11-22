package com.challenge.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Job {

    MGMT("경영·비즈니스"),           // Management
    DEV("개발"),                    // Development
    PLNO("기획·운영"),              // Planning & Operations
    DATA_AI("데이터·AI·ML"),        // Data, AI, Machine Learning
    DESIGN("디자인"),               // Design
    MKT_ADS("마케팅·광고"),         // Marketing & Ads
    FIN_CONS("금융·컨설팅"),         // Finance & Consulting
    MEDIA("미디어"),                // Media
    ECOM_RETAIL("이커머스·리테일"), // E-commerce & Retail
    HR_LABOR("인사·채용·노무"),      // Human Resources & Labor
    SALES("고객·영업"),             // Sales & Customer Support
    RND("연구·R&D"),                // Research & Development
    ENG("엔지니어링"),              // Engineering
    ACC_FIN("회계·재무"),           // Accounting & Finance
    PROD_QUAL("생산·품질"),         // Production & Quality
    GAME_DEV("게임 기획·개발"),      // Game Planning & Development
    LOG_PURCH("물류·구매"),         // Logistics & Procurement
    EDU("교육"),                    // Education
    MED_BIO("의료·제약·바이오"),     // Medical, Pharmaceutical & Bio
    GOV_WEL_ENV("공공·복지·환경");   // Government, Welfare & Environment

    private final String description;

}

