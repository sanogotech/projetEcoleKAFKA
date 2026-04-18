package com.guce.audit.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AuditRecordRequest(
        @NotNull UUID declarationId,
        @NotBlank @Size(max = 128) String eventType,
        @NotBlank @Size(max = 64) String actor,
        @NotBlank @Size(max = 50_000) String payloadJson,
        @Schema(description = "ISO-8601, défaut = maintenant") String occurredAt) {
}
