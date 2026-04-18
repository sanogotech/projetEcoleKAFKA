package com.guce.manifest.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record ManifestResponse(
        @Schema(format = "uuid") UUID id,
        String manifestNumber,
        String vesselName,
        Instant arrivalDate,
        Instant createdAt) {
}
