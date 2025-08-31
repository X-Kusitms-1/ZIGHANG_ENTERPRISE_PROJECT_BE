package com.project.zighang.domain.company.repository;

import com.project.zighang.domain.company.entity.Company;
import com.project.zighang.domain.company.enumerate.CompanyType;
import com.project.zighang.domain.company.enumerate.JobGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query(
            value = """
        SELECT DISTINCT c
        FROM Company c
        WHERE
          (:types IS NULL OR c.companyType IN :types)
          AND (:jobGroups IS NULL OR EXISTS (
                SELECT 1 FROM Company c2 JOIN c2.jobGroups j
                WHERE c2 = c AND j IN :jobGroups
          ))
          AND (:regionCodes IS NULL OR EXISTS (
                SELECT 1
                FROM CompanyRegionMap m
                JOIN m.region r
                LEFT JOIN r.parent p
                WHERE m.company = c
                  AND (r.code IN :regionCodes OR (p IS NOT NULL AND p.code IN :regionCodes))
          ))
        """,
            countQuery = """
        SELECT COUNT(DISTINCT c)
        FROM Company c
        WHERE
          (:types IS NULL OR c.companyType IN :types)
          AND (:jobGroups IS NULL OR EXISTS (
                SELECT 1 FROM Company c2 JOIN c2.jobGroups j
                WHERE c2 = c AND j IN :jobGroups
          ))
          AND (:regionCodes IS NULL OR EXISTS (
                SELECT 1
                FROM CompanyRegionMap m
                JOIN m.region r
                LEFT JOIN r.parent p
                WHERE m.company = c
                  AND (r.code IN :regionCodes OR (p IS NOT NULL AND p.code IN :regionCodes))
          ))
        """
    )
    Page<Company> searchByFilters(
            @Param("types") Set<CompanyType> types,
            @Param("jobGroups") Set<JobGroup> jobGroups,
            @Param("regionCodes") Set<String> regionCodes,
            Pageable pageable
    );
}
