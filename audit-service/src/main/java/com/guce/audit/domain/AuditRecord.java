package com.guce.audit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_records")
@Getter
@Setter
public class AuditRecord {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID declarationId;

    @Column(nullable = false, length = 128)
    private String eventType;

    @Column(nullable = false, length = 64)
    private String actor;

    @Column(nullable = false, columnDefinition = "CLOB")
    private String payloadJson;

    @Column(nullable = false)
    private Instant occurredAt;
}
