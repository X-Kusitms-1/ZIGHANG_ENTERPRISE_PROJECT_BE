package com.project.zighang.domain.company.repository;

import com.project.zighang.domain.company.entity.Region;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface RegionRepository extends JpaRepository<Region, Long> {

    /** (선택) 시/도 id를 넣으면 그 하위 시/군/구 id까지 포함해서 반환 */
    @Query("select r.id from Region r where r.id in :ids or r.parent.id in :ids")
    Set<Long> expandToChildren(@Param("ids") Set<Long> ids);

    /** (선택) UI가 코드(예: SEOUL, SEOUL_GANGNAM_GU)를 보낼 때 편의용 */
    @Query("select r.id from Region r where r.code in :codes")
    Set<Long> findIdsByCodes(@Param("codes") Set<String> codes);
}