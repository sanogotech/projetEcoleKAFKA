package com.guce.declaration.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitDeclarationCommand(
        @NotBlank @Size(max = 64) String correlationId,
        @NotBlank @Size(max = 128) String declarantId,
        @NotBlank @Size(max = 16) String customsOfficeCode,
        @Size(max = 64) String referenceNumber,
        @NotBlank @Size(max = 50_000) String payloadJson) {
}
