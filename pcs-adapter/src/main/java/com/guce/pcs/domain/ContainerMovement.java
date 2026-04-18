package com.guce.pcs.domain;

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
@Table(name = "container_movements")
@Getter
@Setter
public class ContainerMovement {

    @Id
    private UUID id;

    @Column(nullable = false, length = 16)
    private String containerNumber;

    private UUID declarationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private MovementType movementType;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(nullable = false)
    private Instant createdAt;
}
