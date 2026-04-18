package com.guce.pcs.api.dto;

import java.time.Instant;
import java.util.UUID;

public record ContainerMovementResponse(
        UUID id,
        String containerNumber,
        UUID declarationId,
        String movementType,
        Instant occurredAt) {
}
