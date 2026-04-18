package com.guce.payment.api.dto;

import com.guce.payment.domain.PaymentChannel;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConfirmPaymentRequest(
        @NotNull BigDecimal amount,
        @NotNull PaymentChannel channel) {
}
