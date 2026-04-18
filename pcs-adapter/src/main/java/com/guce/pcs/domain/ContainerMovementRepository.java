package com.guce.pcs.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContainerMovementRepository extends JpaRepository<ContainerMovement, UUID> {
}
