package com.project.zighang.domain.company.entity;

import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "company_news",
        indexes = {
                @Index(name = "idx_company_news_company_id", columnList = "company_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_company_news_urlhash", columnNames = "url_hash")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyNews extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", foreignKey = @ForeignKey(name = "fk_company_news_company"))
    private Company company;

    @Column(nullable = false, columnDefinition = "varchar(1000)")
    private String title;

    @Column(columnDefinition = "varchar(2048)")
    private String url;

    private LocalDate publishedAt;

    @Column(columnDefinition = "varchar(2048)")
    private String thumbnailUrl;
}