package com.tekton.challenge.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekton.challenge.backend.model.CalculationLog;
import com.tekton.challenge.backend.repository.CalculationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CalculationLogServiceTest {

    @Mock
    private CalculationLogRepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private CalculationLogService logService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Debe guardar el log correctamente cuando la serialización funciona
    @Test
    void shouldSaveLogSuccessfully() throws Exception {

        when(mapper.writeValueAsString(any())).thenReturn("{\"mocked\":true}");

        logService.saveLog("/api/v1/calculator", "test request", "test response", true);

        // Capturamos el objeto guardado
        ArgumentCaptor<CalculationLog> captor = ArgumentCaptor.forClass(CalculationLog.class);
        verify(repository, times(1)).save(captor.capture());

        CalculationLog savedLog = captor.getValue();

        assertEquals("/api/v1/calculator", savedLog.getEndpoint());
        assertTrue(savedLog.isSuccess());
        assertNotNull(savedLog.getRequest());
        assertNotNull(savedLog.getResponse());
        assertNotNull(savedLog.getTimestamp());
    }

    // Debe manejar correctamente un error al serializar request o response
    @Test
    void shouldHandleSerializationErrorGracefully() throws Exception {
        when(mapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("serialization error") {});

        // No debe lanzar excepción
        assertDoesNotThrow(() -> logService.saveLog("/api/test", "req", "res", false));

        // No se debe intentar guardar en DB
        verify(repository, never()).save(any());
    }

    // Verificar que los valores serializados se pasen correctamente al repositorio
    @Test
    void shouldPassSerializedValuesToRepository() throws Exception {
        when(mapper.writeValueAsString("request")).thenReturn("{\"num1\":10}");
        when(mapper.writeValueAsString("response")).thenReturn("{\"result\":100}");

        logService.saveLog("/api/v1/test", "request", "response", true);

        ArgumentCaptor<CalculationLog> captor = ArgumentCaptor.forClass(CalculationLog.class);
        verify(repository).save(captor.capture());

        CalculationLog log = captor.getValue();
        assertEquals("{\"num1\":10}", log.getRequest());
        assertEquals("{\"result\":100}", log.getResponse());
        assertEquals("/api/v1/test", log.getEndpoint());
        assertTrue(log.isSuccess());
    }

    // Verificar que el timestamp se genere correctamente (no nulo ni futuro)
    @Test
    void shouldGenerateCurrentTimestamp() throws Exception {
        when(mapper.writeValueAsString(any())).thenReturn("{}");

        logService.saveLog("/api/v1/time", "req", "res", true);

        ArgumentCaptor<CalculationLog> captor = ArgumentCaptor.forClass(CalculationLog.class);
        verify(repository).save(captor.capture());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime logTime = captor.getValue().getTimestamp();

        assertNotNull(logTime);
        assertFalse(logTime.isAfter(now.plusSeconds(1))); // tolerancia de tiempo
    }
}