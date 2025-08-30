package com.project.zighang.domain.company.entity;

import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(
        name = "company",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_company_name", columnNames = "company_name")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Company extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String companyName;

    @Column(nullable = false, length = 255)
    private String companyNameKr;

    @Column(length = 100)
    private String companyType;

    @Column(length = 100)
    private String industry;

    @Column(length = 100)
    private String location;
}

