package com.project.zighang.domain.company.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(
        name = "company_region_map",
        uniqueConstraints = @UniqueConstraint(name="uk_company_region", columnNames={"company_id","region_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyRegionMap {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="company_id", foreignKey=@ForeignKey(name="fk_cr_company"))
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="region_id", foreignKey=@ForeignKey(name="fk_cr_region"))
    private Region region;

    // 편의 생성자
    public CompanyRegionMap(Company company, Region region) {
        this.company = company;
        this.region = region;
    }
}
