package com.guce.declaration.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requête de soumission d'une DAU")
public record SubmitDeclarationRequest(
        @NotBlank @Size(max = 64)
        @Schema(description = "Identifiant de corrélation bout-en-bout (tracing)", example = "corr-2026-001")
        String correlationId,
        @NotBlank @Size(max = 128)
        @Schema(description = "Identifiant du déclarant / transitaire", example = "TR-88291")
        String declarantId,
        @NotBlank @Size(max = 16)
        @Schema(description = "Code bureau de douane", example = "CIAB1")
        String customsOfficeCode,
        @Size(max = 64)
        @Schema(description = "Référence métier optionnelle", example = "REF-DAU-2026-0001")
        String referenceNumber,
        @NotBlank @Size(max = 50_000)
        @Schema(description = "Corps de déclaration (JSON métier)", example = "{\"lines\":[]}")
        String payloadJson) {
}
