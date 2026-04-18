package com.guce.manifest.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManifestRepository extends JpaRepository<ManifestEntity, UUID> {
}
