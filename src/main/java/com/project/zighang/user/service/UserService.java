package com.project.zighang.user.service;

import com.project.zighang.user.dto.PostUserOnboardingDto;
import com.project.zighang.user.dto.PostUserTodayApplyCountDTO;

public interface UserService {
    void addUserOnboardingInfo(PostUserOnboardingDto request);
    void setUserApplyCount(PostUserTodayApplyCountDTO request);
}
