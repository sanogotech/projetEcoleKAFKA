package com.guce.authorization.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApiAuthorizationRepository extends JpaRepository<ApiAuthorization, UUID> {
}
