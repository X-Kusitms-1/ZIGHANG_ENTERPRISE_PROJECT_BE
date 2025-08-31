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
        uniqueConstraints = @UniqueConstraint(name = "uk_company_name", columnNames = "company_name")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Company extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String companyName;

    @Column(nullable = false, length = 255)
    private String companyNameKr;

    @Column(nullable = false, length = 1200)
    private String companyThumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CompanyType companyType;

    @ElementCollection(fetch = FetchType.LAZY, targetClass = JobGroup.class)
    @CollectionTable(name = "company_job_groups", joinColumns = @JoinColumn(name = "company_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "job_group", length = 50, nullable = false)
    private Set<JobGroup> jobGroups = EnumSet.noneOf(JobGroup.class);   // 직군

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CompanyRegionMap> companyRegions = new HashSet<>();

    // 편의 메서드 (optional)
    public void addRegion(Region region, boolean primary) {
        CompanyRegionMap link = new CompanyRegionMap(this, region);
        this.companyRegions.add(link);
    }
}

