package com.project.zighang.post.service;

import com.project.zighang.post.entity.PostEntity;
import com.project.zighang.post.enumerate.ApplyStatus;
import com.project.zighang.user.entity.UserEntity;

import java.util.List;

public interface PostingFinder {

    List<PostEntity> findPostingsByStatus(UserEntity user, ApplyStatus status);
}
