package com.guce.inspection.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InspectionCaseRepository extends JpaRepository<InspectionCase, UUID> {
}
