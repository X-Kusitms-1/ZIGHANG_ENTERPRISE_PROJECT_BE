package com.project.zighang.user.service;

import com.project.zighang.global.exception.model.BadRequestException;
import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.user.dto.PostUserOnboardingDto;
import com.project.zighang.user.dto.PostUserTodayApplyCountDTO;
import com.project.zighang.user.entity.AddressEntity;
import com.project.zighang.user.entity.IndustryEntity;
import com.project.zighang.user.entity.UserEntity;
import com.project.zighang.user.entity.UserOnboardingEntity;
import com.project.zighang.user.repository.AddressRepository;
import com.project.zighang.user.repository.IndustryRepository;
import com.project.zighang.user.repository.UserOnboardingRepository;
import com.project.zighang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.zighang.global.exception.Error;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserOnboardingRepository userOnboardingRepository;
    private final AddressRepository addressRepository;
    private final IndustryRepository industryRepository;

    @Override
    public void addUserOnboardingInfo(PostUserOnboardingDto request, UserEntity loginUser) {
        Long minCareer = request.minCareer();
        Long maxCareer = request.maxCareer();
        if (minCareer == null || minCareer < 0 || maxCareer == null || maxCareer < 0) {
            throw new BadRequestException(Error.BAD_REQUEST_CAREER_VALUE, Error.BAD_REQUEST_CAREER_VALUE.getMessage());
        }
        if (minCareer > maxCareer) {
            throw new BadRequestException(Error.BAD_REQUEST_CAREER_VALUE, "minCareer는 maxCareer보다 클 수 없습니다.");
        }

        // Onboarding Entity
        userOnboardingRepository.findByUserEntity(loginUser)
                .ifPresentOrElse(
                        onboardingEntity -> {
                            log.error("이미 온보딩 정보가 존재합니다. userId: {}", loginUser.getId());
                        }, () -> {
                            log.info("온보딩 정보가 없어 새로 저장합니다. userId: {}", loginUser.getId());
                            UserOnboardingEntity userOnboardingEntity = UserOnboardingEntity.create(
                                    loginUser, request.minCareer(), request.maxCareer());
                            userOnboardingRepository.save(userOnboardingEntity);
                            log.info("새로운 온보딩 정보를 저장했습니다.");
                        }
                );

        // Address Entity
        addressRepository.deleteAllByUserEntity(loginUser);

        List<AddressEntity> newAddresses = request.addressList().stream()
                .map(dto -> AddressEntity.create(dto.city(), dto.district(), loginUser))
                .collect(Collectors.toList());

        addressRepository.saveAll(newAddresses);

        // Industry Entity
        industryRepository.deleteAllByUserEntity(loginUser);

        List<IndustryEntity> newIndustries = request.industryList().stream()
                .map(dto -> IndustryEntity.create(dto.jobFamily(), dto.role(), loginUser))
                .collect(Collectors.toList());

        industryRepository.saveAll(newIndustries);
    }

    @Override
    public void setUserApplyCount(PostUserTodayApplyCountDTO request, UserEntity loginUser) {
        if (request.applyCount() < 0) {
            throw  new BadRequestException(Error.BAD_REQUEST_APPLY_COUNT_VALUE, Error.BAD_REQUEST_APPLY_COUNT_VALUE.getMessage());
        }

        UserOnboardingEntity userOnboardingEntity = userOnboardingRepository.findByUserEntity(loginUser).orElseThrow(
                () -> new NotFoundException(Error.NOT_FOUND_USER_ONBOARDING, Error.NOT_FOUND_USER_ONBOARDING.getMessage())
        );

        userOnboardingEntity.updateDailyRecommendPostCount(request.applyCount());
    }
}
