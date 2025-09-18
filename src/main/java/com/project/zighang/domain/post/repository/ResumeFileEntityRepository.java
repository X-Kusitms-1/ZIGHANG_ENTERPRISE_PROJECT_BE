package com.project.zighang.domain.post.repository;

import com.project.zighang.domain.post.entity.PostApplyEntity;
import com.project.zighang.domain.post.entity.ResumeFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeFileEntityRepository extends JpaRepository<ResumeFileEntity, Long> {

    @Query("select re from ResumeFileEntity re join fetch re.postApplyEntity where re.postApplyEntity = :postApply")
    List<ResumeFileEntity> findAllByUserEntityWithPostApplyFetch(@Param("postApply") PostApplyEntity postApplyEntity);
}
