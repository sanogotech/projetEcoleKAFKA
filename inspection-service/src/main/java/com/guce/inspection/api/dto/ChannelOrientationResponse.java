package com.guce.inspection.api.dto;

import java.util.UUID;

public record ChannelOrientationResponse(UUID declarationId, String suggestedChannel, String rationale) {
}
