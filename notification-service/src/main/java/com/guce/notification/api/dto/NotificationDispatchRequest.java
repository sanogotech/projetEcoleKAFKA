package com.guce.notification.api.dto;

import com.guce.notification.domain.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationDispatchRequest(
        @NotNull NotificationChannel channel,
        @NotBlank @Size(max = 256) String target,
        @NotBlank @Size(max = 10_000) String body) {
}
