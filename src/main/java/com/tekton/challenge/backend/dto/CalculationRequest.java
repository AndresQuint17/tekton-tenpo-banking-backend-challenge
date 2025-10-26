package com.tekton.challenge.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CalculationRequest(
        @NotNull(message = "El primer número no puede ser nulo")
        @Min(value = 0, message = "El primer número debe ser mayor o igual a 0")
        Double num1,
        @NotNull(message = "El segundo número no puede ser nulo")
        @Min(value = 0, message = "El segundo número debe ser mayor o igual a 0")
        Double num2) {
}
