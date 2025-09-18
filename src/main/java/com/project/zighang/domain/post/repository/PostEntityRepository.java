package com.project.zighang.domain.post.repository;

import com.project.zighang.domain.post.entity.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {

    List<PostEntity> findAllByOrderByViewCountDesc(Pageable pageable);
}
