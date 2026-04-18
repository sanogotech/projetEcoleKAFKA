package com.guce.declaration.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "DAU persistée")
public record DeclarationResponse(
        @Schema(format = "uuid") UUID id,
        String correlationId,
        String declarantId,
        String customsOfficeCode,
        String referenceNumber,
        String status,
        Instant createdAt) {
}
