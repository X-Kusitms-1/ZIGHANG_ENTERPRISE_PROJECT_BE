package com.project.zighang.domain.company.entity;

import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "company_news")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyNews extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", foreignKey = @ForeignKey(name = "fk_company_news_company"))
    private Company company;

    @Column(nullable = false, length = 1000)
    private String title;

    @Column(length = 2000)
    private String url;

        private LocalDate publishedAt;

    @Column(length = 2000)
    private String thumbnailUrl;
}