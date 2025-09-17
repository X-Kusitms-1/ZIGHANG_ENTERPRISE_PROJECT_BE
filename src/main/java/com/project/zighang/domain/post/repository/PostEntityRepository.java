package com.project.zighang.domain.post.repository;

import com.project.zighang.domain.post.entity.PostEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {

    @Query("SELECT pe " +
            "FROM PostEntity pe " +
            "WHERE pe.recruitmentId NOT IN :excludeIds " +
            "ORDER BY FUNCTION('RAND')")
    List<PostEntity> findRandomExcludingIds(@Param("excludeIds") Set<Long> excludeIds, Pageable pageable);

    default Optional<PostEntity> findOneRandomExcludingIds(Set<Long> excludeIds) {
        List<PostEntity> results = findRandomExcludingIds(
                excludeIds.isEmpty() ? Set.of(-1L) : excludeIds,
                PageRequest.of(0, 1)
        );

        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }
}
