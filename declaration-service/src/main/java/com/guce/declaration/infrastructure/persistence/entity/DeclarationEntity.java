package com.guce.declaration.infrastructure.persistence.entity;

import com.guce.declaration.domain.DeclarationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "declarations")
@Getter
@Setter
public class DeclarationEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 64)
    private String correlationId;

    @Column(nullable = false, length = 128)
    private String declarantId;

    @Column(nullable = false, length = 16)
    private String customsOfficeCode;

    @Column(length = 64)
    private String referenceNumber;

    @Column(nullable = false, columnDefinition = "CLOB")
    private String payloadJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private DeclarationStatus status;

    @Column(nullable = false)
    private Instant createdAt;
}
