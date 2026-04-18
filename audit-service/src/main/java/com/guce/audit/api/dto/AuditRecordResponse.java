package com.guce.audit.api.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditRecordResponse(
        UUID id,
        UUID declarationId,
        String eventType,
        String actor,
        String payloadJson,
        Instant occurredAt) {
}
