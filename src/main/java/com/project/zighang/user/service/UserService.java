package com.project.zighang.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.zighang.oauth2.dto.TokenDto;
import com.project.zighang.user.dto.PostUserOnboardingDto;
import com.project.zighang.user.dto.PostUserTodayApplyCountDTO;
import com.project.zighang.user.dto.response.ReportResponse;
import com.project.zighang.user.entity.UserEntity;

public interface UserService {
    void addUserOnboardingInfo(PostUserOnboardingDto request, UserEntity loginUser);

    void setUserApplyCount(PostUserTodayApplyCountDTO request, UserEntity loginUser);

    boolean isUserOnboarded(UserEntity loginUser);

    ReportResponse.ReportDataDto generateUserReport(UserEntity userEntity) throws Exception;

    TokenDto saveDummyUser();
}
