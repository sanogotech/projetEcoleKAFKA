package com.guce.declaration.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

/**
 * Charge utile publiée sur le topic {@code dau.soumise}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DauSoumiseEventPayload(
        String eventType,
        UUID declarationId,
        String correlationId,
        String declarantId,
        String customsOfficeCode,
        String referenceNumber,
        String status,
        Instant occurredAt,
        String payloadJson) {

    public static final String EVENT_TYPE = "DAU_SOUMISE";

    public static DauSoumiseEventPayload from(String payloadJson, UUID id, String correlationId,
            String declarantId, String customsOfficeCode, String referenceNumber, String status, Instant occurredAt) {
        return new DauSoumiseEventPayload(
                EVENT_TYPE,
                id,
                correlationId,
                declarantId,
                customsOfficeCode,
                referenceNumber,
                status,
                occurredAt,
                payloadJson);
    }
}
