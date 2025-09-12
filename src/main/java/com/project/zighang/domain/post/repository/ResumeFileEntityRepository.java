package com.project.zighang.domain.post.repository;

import com.project.zighang.domain.post.entity.ResumeFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeFileEntityRepository extends JpaRepository<ResumeFileEntity, Long> {
}
