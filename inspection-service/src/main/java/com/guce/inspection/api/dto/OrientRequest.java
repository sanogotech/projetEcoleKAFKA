package com.guce.inspection.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrientRequest(@NotNull UUID declarationId) {
}
