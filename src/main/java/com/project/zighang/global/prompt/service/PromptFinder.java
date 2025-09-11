package com.project.zighang.global.prompt.service;

public interface PromptFinder {

    /**
     * 태그로 프롬프트 조회
     * @param tag
     * @return Prompt.content
     */
    String findPromptByTag(String tag);
}
