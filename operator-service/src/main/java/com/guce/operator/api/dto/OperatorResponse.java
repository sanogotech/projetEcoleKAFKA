package com.guce.operator.api.dto;

import java.time.Instant;
import java.util.UUID;

public record OperatorResponse(UUID id, String legalName, String email, String role, Instant createdAt) {
}
