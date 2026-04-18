package com.guce.operator.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OperatorRepository extends JpaRepository<OperatorAccount, UUID> {
}
