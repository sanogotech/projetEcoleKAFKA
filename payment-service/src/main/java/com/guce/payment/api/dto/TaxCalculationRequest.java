package com.guce.payment.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TaxCalculationRequest(
        @NotNull UUID declarationId,
        @Schema(description = "Optionnel — sinon montant de démo") BigDecimal amountXof) {
}
