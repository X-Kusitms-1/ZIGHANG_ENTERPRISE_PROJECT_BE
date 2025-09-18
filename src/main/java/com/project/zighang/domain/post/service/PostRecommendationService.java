package com.project.zighang.domain.post.service;

import com.project.zighang.domain.post.dto.TodayApplyPostsResponseDto;
import com.project.zighang.domain.post.repository.PostEntityRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostRecommendationService {

    private final PostEntityRepository postEntityRepository;

    // 사용자별 추천 데이터 관리
    private final Map<Long, UserRecommendationData> userRecommendations = new ConcurrentHashMap<>();

    /**
     * 사용자별 추천 데이터 관리 내부 클래스
     */
    @Data
    private static class UserRecommendationData {
        private List<TodayApplyPostsResponseDto> currentDisplayList = new CopyOnWriteArrayList<>();
        private Queue<TodayApplyPostsResponseDto> reservePool = new ConcurrentLinkedQueue<>();
        private Set<Long> allViewedIds = ConcurrentHashMap.newKeySet();

        public void addToViewed(Long id) {
            allViewedIds.add(id);
        }

        public void addAllToViewed(Collection<Long> ids) {
            allViewedIds.addAll(ids);
        }
    }

    /**
     * 사용자 추천 초기화 (표시 리스트 + 예비 풀)
     */
    public void initializeUserRecommendations(
            Long userId,
            List<TodayApplyPostsResponseDto> displayList,
            List<TodayApplyPostsResponseDto> reservePool) {

        UserRecommendationData data = new UserRecommendationData();

        // 표시 리스트 설정
        data.setCurrentDisplayList(new CopyOnWriteArrayList<>(displayList));

        // 예비 풀 설정
        data.setReservePool(new ConcurrentLinkedQueue<>(reservePool));

        // 모든 ID를 조회 기록에 추가
        displayList.forEach(item -> data.addToViewed(item.recruitmentId()));
        reservePool.forEach(item -> data.addToViewed(item.recruitmentId()));

        userRecommendations.put(userId, data);

        log.info("[추천 초기화] User:{} 표시:{} 예비:{} 전체조회기록:{}",
                userId, displayList.size(), reservePool.size(), data.getAllViewedIds().size());
    }

    /**
     * 현재 표시중인 리스트 조회
     */
    public List<TodayApplyPostsResponseDto> getCurrentDisplayList(Long userId) {
        UserRecommendationData data = userRecommendations.get(userId);

        if (data == null) {
            log.warn("[표시 리스트 조회] User:{} 데이터 없음", userId);
            return new ArrayList<>();
        }

        return new ArrayList<>(data.getCurrentDisplayList());
    }

    /**
     * 현재 표시중인 리스트 업데이트
     */
    public void updateCurrentDisplayList(Long userId, List<TodayApplyPostsResponseDto> updatedList) {
        UserRecommendationData data = userRecommendations.computeIfAbsent(
                userId, k -> new UserRecommendationData()
        );

        data.setCurrentDisplayList(new CopyOnWriteArrayList<>(updatedList));

        // 업데이트된 ID들도 조회 기록에 추가
        updatedList.forEach(item -> data.addToViewed(item.recruitmentId()));

        log.info("[표시 리스트 업데이트] User:{} 크기:{}", userId, updatedList.size());
    }

    /**
     * 예비 풀에서 하나 가져오기 (FIFO)
     */
    public TodayApplyPostsResponseDto getAndRemoveFromReservePool(Long userId) {
        UserRecommendationData data = userRecommendations.get(userId);

        if (data == null) {
            log.warn("[예비 풀 조회] User:{} 데이터 없음", userId);
            return null;
        }

        TodayApplyPostsResponseDto item = data.getReservePool().poll();

        if (item != null) {
            log.info("[예비 풀 사용] User:{} 공고:{} 남은개수:{}",
                    userId, item.recruitmentId(), data.getReservePool().size());
        } else {
            log.warn("[예비 풀 소진] User:{}", userId);
        }

        return item;
    }

    /**
     * 예비 풀 재충전
     */
    public void refillReservePool(Long userId, List<TodayApplyPostsResponseDto> newItems) {
        UserRecommendationData data = userRecommendations.computeIfAbsent(
                userId, k -> new UserRecommendationData()
        );

        List<TodayApplyPostsResponseDto> filteredItems = newItems.stream()
                .filter(item -> !data.getAllViewedIds().contains(item.recruitmentId()))
                .toList();

        // 예비 풀에 추가
        data.getReservePool().addAll(filteredItems);

        // 조회 기록에도 추가
        filteredItems.forEach(item -> data.addToViewed(item.recruitmentId()));

        log.info("[예비 풀 재충전] User:{} 추가:{} 총:{}",
                userId, filteredItems.size(), data.getReservePool().size());
    }

    /**
     * 예비 풀 크기 조회
     */
    public int getReservePoolSize(Long userId) {
        UserRecommendationData data = userRecommendations.get(userId);

        if (data == null) {
            return 0;
        }

        return data.getReservePool().size();
    }

    /**
     * 사용자 추천 데이터 전체 삭제
     */
    public void clearUserHistory(Long userId) {
        userRecommendations.remove(userId);
        log.info("[추천 데이터 삭제] User:{}", userId);
    }
}