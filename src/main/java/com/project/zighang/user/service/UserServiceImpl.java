package com.project.zighang.user.service;

import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.user.dto.PostUserOnboardingDto;
import com.project.zighang.user.entity.UserEntity;
import com.project.zighang.user.entity.UserOnboardingEntity;
import com.project.zighang.user.repository.UserOnboardingRepository;
import com.project.zighang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.zighang.global.exception.Error;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserOnboardingRepository userOnboardingRepository;

    @Override
    public void addUserOnboardingInfo(PostUserOnboardingDto request) {
        Long userId = request.userId();

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(Error.NOT_FOUND_USER, Error.NOT_FOUND_USER.getMessage())
        );

        userOnboardingRepository.findByUserEntity(userEntity)
                .ifPresentOrElse(
                        onboardingEntity -> {
                            log.info("온보딩 정보가 존재하여 새로운 정보로 수정합니다. userId: {}", userId);
                            onboardingEntity.updateInfo(request.career(), request.address(), request.industry());
                        }, () -> {
                            log.info("온보딩 정보가 없어 새로 저장합니다. userId: {}", userId);
                            UserOnboardingEntity userOnboardingEntity = UserOnboardingEntity.create(
                                    userEntity, request.career(), request.address(), request.industry());
                            userOnboardingRepository.save(userOnboardingEntity);
                            log.info("새로운 온보딩 정보를 저장했습니다.");
                        }
                );
    }
}
