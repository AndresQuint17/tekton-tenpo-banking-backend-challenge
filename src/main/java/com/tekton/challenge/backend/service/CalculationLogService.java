package com.tekton.challenge.backend.service;

import com.tekton.challenge.backend.model.CalculationLog;
import com.tekton.challenge.backend.repository.CalculationLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CalculationLogService {

    private final CalculationLogRepository repository;
    private final ObjectMapper mapper;

    public CalculationLogService(CalculationLogRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Async("taskExecutor")
    public void saveLog(String endpoint, Object request, Object response, boolean success) {
        try {
            String requestJson = mapper.writeValueAsString(request);
            String responseJson = mapper.writeValueAsString(response);
            CalculationLog log = new CalculationLog(LocalDateTime.now(), endpoint, requestJson, responseJson, success);
            repository.save(log);
        } catch (Exception e) {
            System.err.println("Error guardando log: " + e.getMessage());
        }
    }
}
