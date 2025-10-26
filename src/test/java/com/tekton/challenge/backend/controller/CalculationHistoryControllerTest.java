package com.tekton.challenge.backend.controller;

import com.tekton.challenge.backend.dto.CalculationHistoryResponse;
import com.tekton.challenge.backend.service.CalculationHistoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculationHistoryController.class)
class CalculationHistoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalculationHistoryService historyService;

    // Retornar historial de llamadas exitosamente
    @Test
    void shouldReturnsPageOfHistoryResponses() throws Exception {

        CalculationHistoryResponse response1 = new CalculationHistoryResponse(
                LocalDateTime.now(), "/api/v1/calculator", "{\"num1\":10,\"num2\":5}", "{\"result\":15}", true
        );
        CalculationHistoryResponse response2 = new CalculationHistoryResponse(
                LocalDateTime.now().minusMinutes(5), "/api/v1/calculator", "{\"num1\":20,\"num2\":10}", "{\"result\":30}", true
        );

        Page<CalculationHistoryResponse> page = new PageImpl<>(List.of(response1, response2), PageRequest.of(0, 10), 2);
        Mockito.when(historyService.getLogs(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/v1/history")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].endpoint").value("/api/v1/calculator"))
                .andExpect(jsonPath("$.content[0].success").value(true));
    }

    // Retornar error de servidor cuando el servicio falla
    @Test
    void shouldReturnsInternalServerErrorWhenServiceFails() throws Exception {

        Mockito.when(historyService.getLogs(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/v1/history")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // Validar que los parámetros enviados son incorrectos
    @Test
    void shouldReturnsBadRequestWhenInvalidPageOrSize() throws Exception {
        mockMvc.perform(get("/api/v1/history")
                        .param("page", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Parámetros inválidos"))
                .andExpect(jsonPath("$.message").exists());
    }

}