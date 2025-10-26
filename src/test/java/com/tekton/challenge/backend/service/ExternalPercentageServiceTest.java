package com.tekton.challenge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExternalPercentageServiceTest {
    private Random mockRandom;
    private ExternalPercentageService service;

    @BeforeEach
    void setUp() {
        mockRandom = mock(Random.class);
        service = new ExternalPercentageService() {
            @Override
            public double getDynamicPercentage() {
                // Sobreescritira para inyectar el mock en lugar del Random interno
                try {
                    var field = ExternalPercentageService.class.getDeclaredField("random");
                    field.setAccessible(true);
                    field.set(this, mockRandom);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return super.getDynamicPercentage();
            }
        };
    }

    // Debe lanzar excepción cuando random.nextDouble() < 0.3
    @Test
    void shouldThrowExceptionWhenRandomValueIsBelowThreshold() {
        when(mockRandom.nextDouble()).thenReturn(0.25); // Menor a 0.3, debe fallar

        RuntimeException ex = assertThrows(RuntimeException.class, service::getDynamicPercentage);

        assertEquals("Error al consultar el servicio externo", ex.getMessage());
        verify(mockRandom, times(1)).nextDouble();
    }

    // Debe devolver un valor válido (entre 5 y 15) cuando no falla
    @Test
    void shouldReturnValidPercentageWhenRandomValueAboveThreshold() {
        when(mockRandom.nextDouble())
                .thenReturn(0.5)
                .thenReturn(0.8);

        double result = service.getDynamicPercentage();

        assertTrue(result >= 5 && result <= 15,
                "El porcentaje debe estar entre 5 y 15, pero fue: " + result);

        verify(mockRandom, times(2)).nextDouble();
    }

    // Repetir ejecución varias veces para validar consistencia del rango
    @RepeatedTest(10)
    void shouldAlwaysReturnValueWithinRange() {
        ExternalPercentageService service = new ExternalPercentageService();

        try {
            double result = service.getDynamicPercentage();
            assertTrue(result >= 5 && result <= 15,
                    "Valor fuera de rango: " + result);
        } catch (RuntimeException ignored) {
            // Las excepciones son esperadas.
        }
    }
}