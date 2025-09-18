package com.project.zighang.domain.post.service;

import com.project.zighang.domain.post.dto.TodayApplyPostsResponseDto;
import com.project.zighang.domain.post.repository.PostEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@RequiredArgsConstructor
public class PostRecommendationService {

    private final Map<Long, List<TodayApplyPostsResponseDto>> userRecommendations = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> userViewedPostIds = new ConcurrentHashMap<>();

    private final PostEntityRepository postEntityRepository;

    public TodayApplyPostsResponseDto getNextRecommendation(Long userId, Set<Long> currentIds) {
        Set<Long> viewedIds = userViewedPostIds.computeIfAbsent(userId, k -> new HashSet<>());
        viewedIds.addAll(currentIds);

        return postEntityRepository.findOneRandomExcludingIds(viewedIds)
                .map(post -> {
                    viewedIds.add(post.getRecruitmentId());
                    return TodayApplyPostsResponseDto.from(post);
                })
                .orElse(null);
    }

    public void deleteAndCreateUserRecommendations(Long userId, List<TodayApplyPostsResponseDto> request) {
        userRecommendations.put(userId, request);
    }

    public List<TodayApplyPostsResponseDto> getRecommendations(Long userId) {
        return userRecommendations.getOrDefault(userId, new ArrayList<>());
    }

    public void clearUserHistory(Long userId) {
        userViewedPostIds.remove(userId);
        userRecommendations.remove(userId);
    }
}