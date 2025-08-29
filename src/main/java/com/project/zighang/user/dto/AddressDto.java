package com.project.zighang.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AddressDto(
        @Schema(description = "시/도", example = "서울특별시") @NotBlank
        String city,

        @Schema(description = "구/군", example = "강남구") @NotBlank
        String district
) {
}
