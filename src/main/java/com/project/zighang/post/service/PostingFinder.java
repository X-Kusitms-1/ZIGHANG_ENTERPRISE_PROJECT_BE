package com.project.zighang.post.service;

import com.project.zighang.post.entity.PostApplyEntity;
import com.project.zighang.post.enumerate.ApplyStatus;
import com.project.zighang.user.entity.UserEntity;

import java.util.List;

public interface PostingFinder {

    List<PostApplyEntity> findPostingsByStatus(UserEntity user, ApplyStatus status);
}
