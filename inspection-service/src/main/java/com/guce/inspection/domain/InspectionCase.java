package com.guce.inspection.domain;

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
@Table(name = "inspection_cases")
@Getter
@Setter
public class InspectionCase {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID declarationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private InspectionChannel channel;

    @Column(length = 500)
    private String notes;

    private Instant scheduledAt;

    @Column(nullable = false)
    private Instant createdAt;
}
