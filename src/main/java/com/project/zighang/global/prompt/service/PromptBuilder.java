package com.project.zighang.global.prompt.service;

import com.project.zighang.global.exception.Error;
import com.project.zighang.global.exception.model.BadRequestException;
import com.project.zighang.domain.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromptBuilder {

    public String buildPromptFromPosts(List<PostEntity> passedPosts, List<PostEntity> rejectedPosts) {
        try {
            String passOcrText = passedPosts.stream()
                    .map(PostEntity::getOcrData)  // ocrData 필드 getter
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n\n"));  // 공고별로 줄바꿈 2개

            String failOcrText = rejectedPosts.stream()
                    .map(PostEntity::getOcrData)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n\n"));

            return """
                    다음은 한 사용자에 대한 지원 이력입니다.
                    - 목표: 이 사용자가 어떤 공고에 강하고/약한지 정량·정성 분석
                    - 주의: 직무가 서로 달라도 클러스터링으로 분리한 뒤 비교
                    
                    [합격 공고 목록]
                    """ + passOcrText + """
                    
                    [불합격 공고 목록]
                    """ + failOcrText + """
                    
                    출력은 반드시 지정 JSON 스키마로만 반환해 주세요.
                    """;

        } catch (Exception e) {
            throw new BadRequestException(Error.PROMPT_BUILD_ERROR, e.getMessage());
        }
    }
}