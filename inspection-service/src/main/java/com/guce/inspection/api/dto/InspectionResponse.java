package com.guce.inspection.api.dto;

import java.time.Instant;
import java.util.UUID;

public record InspectionResponse(
        UUID id,
        UUID declarationId,
        String channel,
        String notes,
        Instant scheduledAt,
        Instant createdAt) {
}
