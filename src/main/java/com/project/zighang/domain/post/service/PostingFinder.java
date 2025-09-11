package com.project.zighang.domain.post.service;

import com.project.zighang.domain.post.entity.PostEntity;
import com.project.zighang.domain.post.enumerate.ApplyStatus;
import com.project.zighang.domain.user.entity.UserEntity;

import java.time.LocalDate;
import java.util.List;

public interface PostingFinder {

    List<PostEntity> findPostingsByStatus(UserEntity user, ApplyStatus status);

    List<PostEntity> findPostingsByStatusAndDateRange(UserEntity user, ApplyStatus status, LocalDate startDate, LocalDate endDate);
}
