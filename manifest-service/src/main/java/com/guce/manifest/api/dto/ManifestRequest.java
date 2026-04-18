package com.guce.manifest.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dépôt d'e-manifeste")
public record ManifestRequest(
        @NotBlank @Size(max = 64) @Schema(example = "MAN-2026-0001") String manifestNumber,
        @Size(max = 128) @Schema(example = "MV Atlantic Star") String vesselName,
        @Schema(description = "ISO-8601") String arrivalDate,
        @NotBlank @Size(max = 50_000) @Schema(description = "Lignes de cargaison (JSON)") String linesJson) {
}
