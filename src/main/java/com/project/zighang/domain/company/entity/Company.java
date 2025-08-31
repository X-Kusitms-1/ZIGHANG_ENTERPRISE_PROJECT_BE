package com.project.zighang.domain.company.entity;

import com.project.zighang.domain.company.enumerate.CompanyType;
import com.project.zighang.domain.company.enumerate.JobGroup;
import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;



import java.util.EnumSet;
import java.util.Set;

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

    // 기업 형태: 다중 선택
    @ElementCollection(fetch = FetchType.LAZY, targetClass = CompanyType.class)
    @CollectionTable(name = "company_types", joinColumns = @JoinColumn(name = "company_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "company_type", length = 50, nullable = false)
    private Set<CompanyType> types = EnumSet.noneOf(CompanyType.class);

    // 직군: 다중 선택
    @ElementCollection(fetch = FetchType.LAZY, targetClass = JobGroup.class)
    @CollectionTable(name = "company_job_groups", joinColumns = @JoinColumn(name = "company_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "job_group", length = 50, nullable = false)
    private Set<JobGroup> jobGroups = EnumSet.noneOf(JobGroup.class);

    // 지역: 코드 테이블과 다대다
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "company_regions",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id"))
    private Set<Region> regions = new HashSet<>();
}

