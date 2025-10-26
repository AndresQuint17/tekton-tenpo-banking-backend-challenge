package com.tekton.challenge.backend.exception;

import com.tekton.challenge.backend.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/calculations");
        webRequest = new ServletWebRequest(request);
    }

    // Controlar excepcion de servicio externo
    @Test
    void shouldHandleExternalServiceUnavailableException() {

        ExternalServiceUnavailableException exception =
                new ExternalServiceUnavailableException("El servicio externo falló");

        ResponseEntity<ErrorResponse> response =
                handler.handleExternalServiceUnavailable(exception, webRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(503);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Servicio externo no disponible");
        assertThat(response.getBody().getMessage()).isEqualTo("El servicio externo falló");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/calculations");
        assertThat(response.getBody().getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    // Controlar exceptiones desconocidas
    @Test
    void shouldHandleGenericException() {

        Exception exception = new Exception("Error inesperado");

        ResponseEntity<ErrorResponse> response =
                handler.handleGenericException(exception, webRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Error interno del servidor");
        assertThat(response.getBody().getMessage()).isEqualTo("Error inesperado");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/calculations");
        assertThat(response.getBody().getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}