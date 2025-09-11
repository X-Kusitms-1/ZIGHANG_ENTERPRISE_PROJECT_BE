package com.project.zighang.global.prompt.repository;

import com.project.zighang.global.prompt.entity.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PromptRepository extends JpaRepository<Prompt, Long> {

    @Query("SELECT p FROM Prompt p WHERE p.tag = :tag")
    Optional<Prompt> findByTag(@Param("tag") String tag);

    @Query("SELECT p.content FROM Prompt p WHERE p.tag = :tag")
    Optional<String> findContentByTag(@Param("tag") String tag);
}
