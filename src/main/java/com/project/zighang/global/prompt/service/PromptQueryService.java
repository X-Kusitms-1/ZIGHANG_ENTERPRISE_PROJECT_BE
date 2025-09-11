package com.project.zighang.global.prompt.service;

import com.project.zighang.global.exception.Error;
import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.global.prompt.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromptQueryService implements PromptFinder {

    private final PromptRepository promptRepository;

    @Override
    public String findPromptByTag(String tag) {
        return promptRepository.findContentByTag(tag)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_PROMPT, Error.NOT_FOUND_PROMPT.getMessage()));
    }
}
