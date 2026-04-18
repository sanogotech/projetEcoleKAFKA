package com.guce.payment.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(UUID id, UUID declarationId, BigDecimal amount, String channel, String status) {
}
