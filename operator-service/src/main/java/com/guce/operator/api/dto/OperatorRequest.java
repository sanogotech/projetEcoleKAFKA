package com.guce.operator.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OperatorRequest(
        @NotBlank @Size(max = 128) String legalName,
        @NotBlank @Email @Size(max = 256) String email,
        @NotBlank @Size(max = 64) String role) {
}
