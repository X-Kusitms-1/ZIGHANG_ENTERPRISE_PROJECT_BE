package com.project.zighang.user.service;

import com.project.zighang.user.dto.PostUserOnboardingDto;
import com.project.zighang.user.dto.PostUserTodayApplyCountDTO;
import com.project.zighang.user.entity.UserEntity;

public interface UserService {
    void addUserOnboardingInfo(PostUserOnboardingDto request, UserEntity loginUser);
    void setUserApplyCount(PostUserTodayApplyCountDTO request, UserEntity loginUser);
}
