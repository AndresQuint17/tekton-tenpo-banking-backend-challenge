package com.tekton.challenge.backend.controller;

import com.tekton.challenge.backend.dto.CalculationRequest;
import com.tekton.challenge.backend.dto.CalculationResponse;
import com.tekton.challenge.backend.service.CalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calculator")
@Tag(
        name = "Cálculos",
        description = """
                Endpoints relacionados con operaciones de cálculo que involucran un porcentaje dinámico
                obtenido desde un servicio externo.
                La API aplica un porcentaje adicional a la suma de dos números, almacenando temporalmente
                el porcentaje en caché durante 30 minutos.
                En caso de falla del servicio externo, se utiliza el último valor almacenado;
                si no hay caché, se devuelve un error 503 (Servicio no disponible).
                """
)
@Validated
public class CalculationController {

    private final CalculationService calculationService;

    public CalculationController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @Operation(
            summary = "Realiza el cálculo con porcentaje dinámico",
            description = """
                    Este endpoint recibe dos números (`num1` y `num2`), los suma y aplica un porcentaje
                    adicional obtenido desde un servicio externo.
                    
                    - Si el servicio externo responde correctamente, el valor obtenido reemplaza
                      el existente en caché (válido por 30 minutos).
                    - Si el servicio externo falla, se utiliza el último porcentaje almacenado en caché.
                    - Si no hay porcentaje en caché, se devuelve un error con código 503 (Servicio no disponible).
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de entrada para realizar el cálculo.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CalculationRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de solicitud",
                                    value = """
                                            {
                                              "num1": 1000,
                                              "num2": 500
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cálculo realizado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CalculationResponse.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de respuesta exitosa",
                                    value = """
                                            {
                                              "num1": 1000,
                                              "num2": 500,
                                              "percentageApplied": 10,
                                              "result": 1650
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida. Los parámetros no cumplen el formato esperado.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Error de validación",
                                    value = """
                                            {
                                              "timestamp": "2025-10-26T10:42:00",
                                              "status": 400,
                                              "error": "Error de validación de entrada",
                                              "message": "num2: El segundo número no puede ser nulo; num1: El primer número no puede ser nulo",
                                              "path": "/api/v1/calculator"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Servicio externo no disponible y no existe valor en caché.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Error de servicio externo",
                                    value = """
                                            {
                                              "timestamp": "2025-10-26T10:45:12",
                                              "status": 503,
                                              "error": "Servicio externo no disponible",
                                              "message": "El servicio externo no está disponible y no existe un valor en caché.",
                                              "path": "/api/v1/calculator"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping
    public CalculationResponse calculate(@Valid @RequestBody CalculationRequest request) {
        return calculationService.calculate(request);
    }
}
