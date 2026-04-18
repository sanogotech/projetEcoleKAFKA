package com.guce.authorization.domain;

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
@Table(name = "api_authorizations")
@Getter
@Setter
public class ApiAuthorization {

    @Id
    private UUID id;

    @Column(nullable = false, length = 128)
    private String applicantId;

    @Column(nullable = false, columnDefinition = "CLOB")
    private String goodsDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AuthorizationStatus status;

    private Instant expiresAt;

    @Column(nullable = false)
    private Instant createdAt;
}
