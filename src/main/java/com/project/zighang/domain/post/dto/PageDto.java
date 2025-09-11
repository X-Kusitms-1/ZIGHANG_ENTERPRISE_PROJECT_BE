package com.project.zighang.domain.post.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public record PageDto<T>(
        List<T> content,
        int totalPages,
        long totalElements,
        int pageNumber,
        int size,
        boolean isFirstPage,
        boolean isLastPage
) {
    public static <T> PageDto<T> from(Page<T> page) {
        return new PageDto<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getSize(),
                page.isFirst(),
                page.isLast()
        );
    }
}