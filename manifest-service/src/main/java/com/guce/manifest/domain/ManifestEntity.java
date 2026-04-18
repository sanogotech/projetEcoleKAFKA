package com.guce.manifest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "manifests")
@Getter
@Setter
public class ManifestEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 64)
    private String manifestNumber;

    @Column(length = 128)
    private String vesselName;

    private Instant arrivalDate;

    @Column(nullable = false, columnDefinition = "CLOB")
    private String linesJson;

    @Column(nullable = false)
    private Instant createdAt;
}
