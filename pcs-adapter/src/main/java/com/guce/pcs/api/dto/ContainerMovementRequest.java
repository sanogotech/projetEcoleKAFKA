package com.guce.pcs.api.dto;

import com.guce.pcs.domain.MovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ContainerMovementRequest(
        @NotBlank @Size(max = 16) String containerNumber,
        UUID declarationId,
        @NotNull MovementType movementType,
        String occurredAt) {
}
