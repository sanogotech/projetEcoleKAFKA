package com.guce.inspection.api.dto;

import com.guce.inspection.domain.InspectionChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InspectionRequest(
        @NotNull UUID declarationId,
        @NotNull InspectionChannel channel,
        @Schema(description = "ISO-8601 optionnel") String scheduledAt,
        String notes) {
}
