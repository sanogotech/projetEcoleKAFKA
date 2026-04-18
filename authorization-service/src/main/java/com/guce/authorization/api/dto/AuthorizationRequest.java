package com.guce.authorization.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorizationRequest(
        @NotBlank @Size(max = 128) String applicantId,
        @NotBlank @Size(max = 20_000) String goodsDescription,
        @Schema(description = "ISO-8601, optionnel") String expiresAt) {
}
