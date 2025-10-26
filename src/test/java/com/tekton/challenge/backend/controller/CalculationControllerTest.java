package com.tekton.challenge.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekton.challenge.backend.dto.CalculationRequest;
import com.tekton.challenge.backend.dto.CalculationResponse;
import com.tekton.challenge.backend.exception.ExternalServiceUnavailableException;
import com.tekton.challenge.backend.exception.GlobalExceptionHandler;
import com.tekton.challenge.backend.service.CalculationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculationController.class)
@Import(GlobalExceptionHandler.class)
class CalculationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalculationService calculationService;

    @Autowired
    private ObjectMapper objectMapper;

    // Calcular exitosamente
    @Test
    void calculate_ReturnsOk_WhenCalculationIsSuccessful() throws Exception {

        CalculationRequest request = new CalculationRequest(5.0, 5.0);
        CalculationResponse response = new CalculationResponse(5.0, 5.0, 10.0, 11.0);

        when(calculationService.calculate(any(CalculationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/calculator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.num1").value(5.0))
                .andExpect(jsonPath("$.num2").value(5.0))
                .andExpect(jsonPath("$.percentageApplied").value(10.0))
                .andExpect(jsonPath("$.result").value(11.0));
    }

    // Responder error de servicio externo
    @Test
    void calculate_ReturnsServiceUnavailable_WhenExternalServiceFails() throws Exception {

        CalculationRequest request = new CalculationRequest(10.0, 20.0);
        when(calculationService.calculate(any(CalculationRequest.class)))
                .thenThrow(new ExternalServiceUnavailableException("El servicio externo no disponible y no hay valor en caché"));

        mockMvc.perform(post("/api/v1/calculator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Servicio externo no disponible"))
                .andExpect(jsonPath("$.message").value("El servicio externo no disponible y no hay valor en caché"));
    }

    // Responder fallo inesperado en servidor
    @Test
    void calculate_ReturnsInternalServerError_WhenUnexpectedExceptionOccurs() throws Exception {

        CalculationRequest request = new CalculationRequest(3.0, 4.0);
        when(calculationService.calculate(any(CalculationRequest.class)))
                .thenThrow(new RuntimeException("Fallo inesperado"));

        mockMvc.perform(post("/api/v1/calculator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error interno del servidor"))
                .andExpect(jsonPath("$.message").value("Fallo inesperado"));
    }
}