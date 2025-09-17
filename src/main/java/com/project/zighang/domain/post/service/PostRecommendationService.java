package com.project.zighang.domain.post.service;

import com.project.zighang.domain.post.dto.TodayApplyPostsResponseDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PostRecommendationService {

    private final Map<Long, List<TodayApplyPostsResponseDto>> userRecommendations = new ConcurrentHashMap<>();

    public void deleteAndCreateUserRecommendations(Long userId, List<TodayApplyPostsResponseDto> request) {
        userRecommendations.put(userId, request);
    }

    public List<TodayApplyPostsResponseDto> getRecommendations(Long userId) {
        return userRecommendations.getOrDefault(userId, new ArrayList<>());
    }
}