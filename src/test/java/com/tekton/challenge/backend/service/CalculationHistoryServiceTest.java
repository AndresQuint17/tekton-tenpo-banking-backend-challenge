package com.tekton.challenge.backend.service;

import com.tekton.challenge.backend.dto.CalculationHistoryResponse;
import com.tekton.challenge.backend.model.CalculationLog;
import com.tekton.challenge.backend.repository.CalculationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalculationHistoryServiceTest {
    @Mock
    private CalculationLogRepository repository;

    @InjectMocks
    private CalculationHistoryService service;

    private CalculationLog log1;
    private CalculationLog log2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        log1 = new CalculationLog(LocalDateTime.now().minusMinutes(5), "/api/v1/calc", "req1", "res1", true);
        log2 = new CalculationLog(LocalDateTime.now(), "/api/v1/calc", "req2", "res2", false);
    }

    // Verifica que los resultados se transformen correctamente a DTOs.
    @Test
    void shouldReturnsMappedPageWhenRepositoryHasData() {

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "timestamp"));
        List<CalculationLog> logs = List.of(log1, log2);
        Page<CalculationLog> logPage = new PageImpl<>(logs, pageable, logs.size());
        when(repository.findAll(pageable)).thenReturn(logPage);

        Page<CalculationHistoryResponse> result = service.getLogs(0, 2);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertNotNull(result.getContent().getFirst());
        verify(repository, times(1)).findAll(pageable);
    }

    // Verifica que se use orden descendente por timestamp y los parámetros correctos de paginación.
    @Test
    void shouldUsesCorrectSortOrder() {

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        service.getLogs(1, 5);

        verify(repository).findAll(pageableCaptor.capture());
        Pageable captured = pageableCaptor.getValue();

        assertEquals(1, captured.getPageNumber());
        assertEquals(5, captured.getPageSize());

        Sort.Order order = captured.getSort().getOrderFor("timestamp");
        assertNotNull(order);
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }

    // Verifica que el metodo funcione correctamente cuando el repositorio no tiene registros.
    @Test
    void getLogs_ReturnsEmptyPage_WhenRepositoryIsEmpty() {

        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Page<CalculationHistoryResponse> result = service.getLogs(0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll(any(Pageable.class));
    }
}