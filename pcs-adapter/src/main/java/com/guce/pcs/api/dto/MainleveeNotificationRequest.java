package com.guce.pcs.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MainleveeNotificationRequest(
        @NotNull UUID declarationId,
        @NotNull UUID mainleveeId) {
}
