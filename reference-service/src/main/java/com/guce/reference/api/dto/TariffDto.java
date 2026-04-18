package com.guce.reference.api.dto;

import java.math.BigDecimal;

public record TariffDto(String shCode, BigDecimal dutyRatePercent, String currency) {
}
