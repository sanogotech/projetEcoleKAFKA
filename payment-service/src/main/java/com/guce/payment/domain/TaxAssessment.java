package com.guce.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tax_assessments")
@Getter
@Setter
public class TaxAssessment {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID declarationId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amountXof;

    @Column(nullable = false, length = 8)
    private String currency;

    @Column(nullable = false)
    private Instant computedAt;
}
