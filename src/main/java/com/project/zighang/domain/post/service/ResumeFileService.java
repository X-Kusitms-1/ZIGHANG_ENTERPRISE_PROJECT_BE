package com.project.zighang.domain.post.service;

import com.project.zighang.domain.post.dto.ResumeFileResponseDto;
import com.project.zighang.domain.post.dto.UploadCompleteRequestDto;
import com.project.zighang.domain.post.entity.PostApplyEntity;
import com.project.zighang.domain.post.entity.PostEntity;
import com.project.zighang.domain.post.entity.ResumeFileEntity;
import com.project.zighang.domain.post.repository.PostApplyEntityRepository;
import com.project.zighang.domain.post.repository.PostEntityRepository;
import com.project.zighang.domain.post.repository.ResumeFileEntityRepository;
import com.project.zighang.domain.user.entity.UserEntity;
import com.project.zighang.global.client.objectStorage.service.NcpPresignedUrlReader;
import com.project.zighang.global.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.zighang.global.exception.Error;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ResumeFileService {

    private final ResumeFileEntityRepository resumeFileEntityRepository;
    private final PostEntityRepository postEntityRepository;
    private final PostApplyEntityRepository postApplyEntityRepository;
    private final NcpPresignedUrlReader ncpPresignedUrlReader;

    public ResumeFileResponseDto completeUpload(UploadCompleteRequestDto request, UserEntity loginUser) {
        PostApplyEntity applyEntity = validateUserAccess(request.recruitmentId(), loginUser);

        log.info("=== 파일 존재 확인 시작 ===");
        log.info("Original objectUrl: {}", request.objectUrl());

        String objectKey = ncpPresignedUrlReader.extractObjectKeyFromUrl(request.objectUrl());
        log.info("Extracted objectKey: {}", objectKey);

        if (!ncpPresignedUrlReader.isFileExists(objectKey)) {
            throw new RuntimeException("파일이 존재하지 않습니다");
        }

        ResumeFileEntity resumeFileEntity = ResumeFileEntity.create(
                request.originalFileName(),
                request.objectUrl(),
                applyEntity
        );

        ResumeFileEntity savedEntity = resumeFileEntityRepository.save(resumeFileEntity);
        return ResumeFileResponseDto.from(savedEntity);
    }

    private PostApplyEntity validateUserAccess(Long recruitmentId, UserEntity loginUser) {
        PostEntity postEntity = postEntityRepository.findById(recruitmentId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_POST, Error.NOT_FOUND_POST.getMessage()));

        return postApplyEntityRepository
                .findPostApplyEntityByPostEntityAndUserEntity(postEntity, loginUser)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_APPLY, Error.NOT_FOUND_APPLY.getMessage()));
    }
}