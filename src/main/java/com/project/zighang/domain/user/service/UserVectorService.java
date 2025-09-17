package com.project.zighang.domain.user.service;

import com.project.zighang.domain.user.entity.UserEntity;

public interface UserVectorService {
    void createUserProfileEmbedding(UserEntity user);
}
