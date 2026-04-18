package com.guce.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaxAssessmentRepository extends JpaRepository<TaxAssessment, UUID> {
}
