package com.guce.payment.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TaxCalculationResponse(UUID id, UUID declarationId, BigDecimal amountXof, String currency) {
}
