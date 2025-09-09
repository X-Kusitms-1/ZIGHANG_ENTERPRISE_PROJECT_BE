package com.project.zighang.global.prompt.dto;

import java.util.List;

public class PromptResponsDto {

    public record prompts(
            List<String> prompts
    ) {}
}
