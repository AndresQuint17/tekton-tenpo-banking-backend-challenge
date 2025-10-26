package com.tekton.challenge.backend.repository;

import com.tekton.challenge.backend.model.CalculationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalculationLogRepository extends JpaRepository<CalculationLog, Long> {
}
