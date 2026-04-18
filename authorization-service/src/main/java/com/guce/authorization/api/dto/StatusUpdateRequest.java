package com.guce.authorization.api.dto;

import com.guce.authorization.domain.AuthorizationStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull AuthorizationStatus status) {
}
