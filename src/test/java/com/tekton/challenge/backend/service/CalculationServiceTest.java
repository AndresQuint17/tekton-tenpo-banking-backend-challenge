package com.tekton.challenge.backend.service;

import com.tekton.challenge.backend.dto.CalculationRequest;
import com.tekton.challenge.backend.dto.CalculationResponse;
import com.tekton.challenge.backend.exception.ExternalServiceUnavailableException;
import com.tekton.challenge.backend.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalculationServiceTest {
    @Mock
    private ExternalPercentageService externalService;

    @Mock
    private PercentageCacheService cacheService;

    @Mock
    private CalculationLogService logService;

    @InjectMocks
    private CalculationService calculationService;

    private CalculationRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new CalculationRequest(10d, 20d);
    }

    // Caso exitoso: servicio externo funciona correctamente
    @Test
    void shouldCalculateSuccessfullyUsingExternalService() {
        when(externalService.getDynamicPercentage()).thenReturn(10.0); // 10%

        CalculationResponse response = calculationService.calculate(request);

        // Verificación de cálculo: (10 + 20) + 30 * (10 / 100) = 33
        assertEquals(10, response.num1());
        assertEquals(20, response.num2());
        assertEquals(10.0, response.percentageApplied());
        assertEquals(33.0, response.result());

        // Verificar las interacciones
        verify(externalService).getDynamicPercentage();
        verify(cacheService).savePercentage(10.0);
        verify(logService).saveLog(Constants.CALCULATE_ENDPOINT, request, response, true);
    }

    // Caso: servicio externo falla pero hay valor en caché
    @Test
    void shouldUseCachedValueWhenExternalServiceFails() {
        when(externalService.getDynamicPercentage()).thenThrow(new RuntimeException("External service failed"));
        when(cacheService.getCachedPercentage()).thenReturn(Optional.of(5.0));

        CalculationResponse response = calculationService.calculate(request);

        // Cálculo con caché: (10 + 20) + 30 * (5 / 100) = 31.5
        assertEquals(10, response.num1());
        assertEquals(20, response.num2());
        assertEquals(5.0, response.percentageApplied());
        assertEquals(31.5, response.result());

        verify(externalService).getDynamicPercentage();
        verify(cacheService).getCachedPercentage();
        verify(logService).saveLog(Constants.CALCULATE_ENDPOINT, request, response, true);
    }

    // Caso: servicio externo falla y no hay valor en caché → lanza excepción
    @Test
    void shouldThrowWhenExternalServiceFailsAndCacheEmpty() {
        when(externalService.getDynamicPercentage()).thenThrow(new RuntimeException("External service failed"));
        when(cacheService.getCachedPercentage()).thenReturn(Optional.empty());

        ExternalServiceUnavailableException exception = assertThrows(
                ExternalServiceUnavailableException.class,
                () -> calculationService.calculate(request)
        );

        assertEquals("El servicio externo no disponible y no hay valor en caché", exception.getMessage());

        // Verificar orden lógico de eventos
        verify(externalService).getDynamicPercentage();
        verify(cacheService).getCachedPercentage();
        verify(logService).saveLog(eq(Constants.CALCULATE_ENDPOINT), eq(request), anyString(), eq(false));
    }

    // Verificar que el caché se actualiza cuando el servicio externo responde correctamente
    @Test
    void shouldUpdateCacheAfterExternalServiceSuccess() {
        when(externalService.getDynamicPercentage()).thenReturn(12.0);

        calculationService.calculate(request);

        verify(cacheService).savePercentage(12.0);
    }

    // Verificar que se registra un error si el servicio externo y el caché fallan
    @Test
    void shouldLogErrorWhenExternalAndCacheFail() {
        when(externalService.getDynamicPercentage()).thenThrow(new RuntimeException("External service failed"));
        when(cacheService.getCachedPercentage()).thenThrow(new RuntimeException("Cache failure"));

        assertThrows(RuntimeException.class, () -> calculationService.calculate(request));

        verify(logService).saveLog(eq(Constants.CALCULATE_ENDPOINT), eq(request), anyString(), eq(false));
    }
}