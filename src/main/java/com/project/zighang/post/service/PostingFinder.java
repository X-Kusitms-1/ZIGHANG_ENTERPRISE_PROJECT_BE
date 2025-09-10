package com.project.zighang.post.service;

import com.project.zighang.post.entity.PostEntity;
import com.project.zighang.post.enumerate.ApplyStatus;
import com.project.zighang.user.entity.UserEntity;

import java.time.LocalDate;
import java.util.List;

public interface PostingFinder {

    List<PostEntity> findPostingsByStatus(UserEntity user, ApplyStatus status);

    List<PostEntity> findPostingsByStatusAndDateRange(UserEntity user, ApplyStatus status, LocalDate startDate, LocalDate endDate);
}
