package com.guce.declaration.infrastructure.persistence;

import com.guce.declaration.infrastructure.persistence.entity.DeclarationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeclarationJpaRepository extends JpaRepository<DeclarationEntity, UUID> {
}
