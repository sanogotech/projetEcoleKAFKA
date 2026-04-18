package com.guce.declaration.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Agrégat métier : déclaration en détail (DAU) soumise au GUCE.
 */
public class Declaration {

    private final UUID id;
    private final String correlationId;
    private final String declarantId;
    private final String customsOfficeCode;
    private final String referenceNumber;
    private final String payloadJson;
    private final DeclarationStatus status;
    private final Instant createdAt;

    public Declaration(
            UUID id,
            String correlationId,
            String declarantId,
            String customsOfficeCode,
            String referenceNumber,
            String payloadJson,
            DeclarationStatus status,
            Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.correlationId = Objects.requireNonNull(correlationId);
        this.declarantId = Objects.requireNonNull(declarantId);
        this.customsOfficeCode = Objects.requireNonNull(customsOfficeCode);
        this.referenceNumber = referenceNumber;
        this.payloadJson = Objects.requireNonNull(payloadJson);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static Declaration newSubmission(
            String correlationId,
            String declarantId,
            String customsOfficeCode,
            String referenceNumber,
            String payloadJson) {
        return new Declaration(
                UUID.randomUUID(),
                correlationId,
                declarantId,
                customsOfficeCode,
                referenceNumber,
                payloadJson,
                DeclarationStatus.SOUMISE,
                Instant.now());
    }

    public UUID getId() {
        return id;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getDeclarantId() {
        return declarantId;
    }

    public String getCustomsOfficeCode() {
        return customsOfficeCode;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public DeclarationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
