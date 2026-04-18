package com.guce.operator.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "operators")
@Getter
@Setter
public class OperatorAccount {

    @Id
    private UUID id;

    @Column(nullable = false, length = 128)
    private String legalName;

    @Column(nullable = false, unique = true, length = 256)
    private String email;

    @Column(nullable = false, length = 64)
    private String role;

    @Column(nullable = false)
    private Instant createdAt;
}
