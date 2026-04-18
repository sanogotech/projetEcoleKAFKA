package com.guce.notification.api.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationDispatchResponse(UUID id, String channel, String target, Instant createdAt) {
}
