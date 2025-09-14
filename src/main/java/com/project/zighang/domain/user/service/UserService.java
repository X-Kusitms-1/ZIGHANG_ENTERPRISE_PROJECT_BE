package com.project.zighang.domain.user.service;

import com.project.zighang.domain.oauth2.dto.TokenDto;
import com.project.zighang.domain.post.dto.PostResponseDto;
import com.project.zighang.domain.user.dto.PostUserOnboardingDto;
import com.project.zighang.domain.user.dto.PostUserTodayApplyCountDTO;
import com.project.zighang.domain.user.dto.request.AccuracyRequest;
import com.project.zighang.domain.user.dto.response.AchievementResponse;
import com.project.zighang.domain.user.dto.response.ReportResponse;
import com.project.zighang.domain.user.dto.response.TodayPostResponseDto;
import com.project.zighang.domain.user.entity.Accuracy;
import com.project.zighang.domain.user.entity.UserEntity;

import java.util.List;

public interface UserService {
    void addUserOnboardingInfo(PostUserOnboardingDto request, UserEntity loginUser);

    void setUserApplyCount(PostUserTodayApplyCountDTO request, UserEntity loginUser);

    boolean isUserOnboarded(UserEntity loginUser);

    ReportResponse.ReportDataDto generateUserReport(UserEntity userEntity) throws Exception;

    TokenDto saveDummyUser();

    ReportResponse.Weekly generateWeeklyReport(UserEntity userEntity, Integer year, Integer month, Integer weekOfMonth) throws Exception;

    boolean checkWeeklyReportExists(UserEntity userEntity, Integer year, Integer month, Integer weekOfMonth);

    Accuracy createAccuracy(UserEntity userEntity, AccuracyRequest.answers answers);

    Accuracy updateAccuracy(UserEntity userEntity, AccuracyRequest.answers answers);

    Accuracy getAccuracy(UserEntity userEntity);

    List<TodayPostResponseDto> getUserTodayPosts(UserEntity loginUser);

    void deleteUserTodayPosts(UserEntity loginUser);

    AchievementResponse getAchievementStatus(UserEntity loginUser);
}
