package com.guce.audit.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditRecordRepository extends JpaRepository<AuditRecord, UUID> {

    List<AuditRecord> findByDeclarationIdOrderByOccurredAtDesc(UUID declarationId);
}
