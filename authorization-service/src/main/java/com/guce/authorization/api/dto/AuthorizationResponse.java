package com.guce.authorization.api.dto;

import java.time.Instant;
import java.util.UUID;

public record AuthorizationResponse(
        UUID id,
        String applicantId,
        String goodsDescription,
        String status,
        Instant expiresAt,
        Instant createdAt) {
}
