package com.project.zighang.domain.company.repository;

import com.project.zighang.domain.company.entity.CompanyNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CompanyNewsRepository extends JpaRepository<CompanyNews, Long> {

    interface NewsSliceRow {
        Long getCompanyId();
        String getTitle();
        String getUrl();
        LocalDate getPublishedAt();
        String getThumbnailUrl();
    }

    @Query(value = """
        SELECT company_id AS companyId,
               title,
               url,
               published_at AS publishedAt,
               thumbnail_url AS thumbnailUrl
        FROM (
            SELECT n.*,
                   ROW_NUMBER() OVER (PARTITION BY n.company_id ORDER BY n.published_at DESC, n.id DESC) rn
            FROM company_news n
            WHERE n.company_id IN (:companyIds)
        ) t
        WHERE t.rn <= :limit
        ORDER BY company_id ASC, published_at DESC, url ASC
    """, nativeQuery = true)
    List<NewsSliceRow> findTopNewsByCompanyIds(
            @Param("companyIds") List<Long> companyIds,
            @Param("limit") int limit
    );
}