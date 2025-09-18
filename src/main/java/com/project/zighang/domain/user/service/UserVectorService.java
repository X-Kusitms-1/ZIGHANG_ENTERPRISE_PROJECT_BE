package com.project.zighang.domain.user.service;

import com.project.zighang.domain.user.entity.UserEntity;

import java.util.List;

public interface UserVectorService {
    List<Double> createUserProfileEmbedding(UserEntity user);
}
