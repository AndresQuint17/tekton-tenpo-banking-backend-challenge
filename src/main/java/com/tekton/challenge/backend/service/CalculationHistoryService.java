package com.tekton.challenge.backend.service;

import com.tekton.challenge.backend.dto.CalculationHistoryResponse;
import com.tekton.challenge.backend.repository.CalculationLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CalculationHistoryService {

    private final CalculationLogRepository repository;

    public CalculationHistoryService(CalculationLogRepository repository) {
        this.repository = repository;
    }

    public Page<CalculationHistoryResponse> getLogs(int page, int size) {
        // Retorna los registros ordenados por fecha descendente
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return repository.findAll(pageRequest)
                .map(CalculationHistoryResponse::fromEntity);
    }
}
