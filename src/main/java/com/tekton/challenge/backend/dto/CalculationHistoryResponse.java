package com.tekton.challenge.backend.dto;

import com.tekton.challenge.backend.model.CalculationLog;

import java.time.LocalDateTime;

public record CalculationHistoryResponse(
        LocalDateTime timestamp,
        String endpoint,
        String parameters,
        String response,
        Boolean success
) {
    public static CalculationHistoryResponse fromEntity(CalculationLog log) {
        return new CalculationHistoryResponse(
                log.getTimestamp(),
                log.getEndpoint(),
                log.getRequest(),
                log.getResponse(),
                log.isSuccess()
        );
    }
}
