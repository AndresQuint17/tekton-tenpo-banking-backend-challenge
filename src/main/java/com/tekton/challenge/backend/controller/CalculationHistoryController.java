package com.tekton.challenge.backend.controller;

import com.tekton.challenge.backend.dto.CalculationHistoryResponse;
import com.tekton.challenge.backend.service.CalculationHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/history")
@Tag(
        name = "Historial de Cálculos",
        description = """
                Endpoints relacionados con la consulta del historial de operaciones realizadas 
                mediante el servicio de cálculo con porcentaje dinámico.  
                Cada registro almacena información como la fecha de la solicitud, 
                los parámetros enviados, el endpoint invocado y la respuesta o error devuelto.
                """
)
public class CalculationHistoryController {

    private final CalculationHistoryService historyService;

    public CalculationHistoryController(CalculationHistoryService historyService) {
        this.historyService = historyService;
    }

    @Operation(
            summary = "Consulta el historial de cálculos realizados",
            description = """
                    Este endpoint permite obtener un listado paginado de las llamadas realizadas al 
                    servicio de cálculo (`/api/v1/calculator`).  
                    
                    Cada registro incluye detalles como:
                    - Fecha y hora de la llamada  
                    - Endpoint invocado  
                    - Parámetros enviados  
                    - Respuesta o error resultante  
                    
                    El listado está paginado para optimizar la consulta.  
                    Los parámetros `page` y `size` controlan el desplazamiento y cantidad de registros por página.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Historial obtenido correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CalculationHistoryResponse.class)),
                            examples = @ExampleObject(
                                    name = "Ejemplo de respuesta",
                                    value = """
                                            {
                                              "content": [
                                                {
                                                  "timestamp": "2025-10-26T10:32:00",
                                                  "endpoint": "/api/v1/calculator",
                                                  "parameters": "{\\"num1\\": 1000, \\"num2\\": 500}",
                                                  "response": "{\\"num1\\":1000,\\"num2\\":500,\\"percentageApplied\\":10,\\"result\\":1650}",
                                                  "success": true
                                                },
                                                {
                                                  "timestamp": "2025-10-26T10:35:00",
                                                  "endpoint": "/api/v1/calculator",
                                                  "parameters": "{\\"num1\\":200, \\"num2\\":300}",
                                                  "response": null,
                                                  "success": false
                                                }
                                              ],
                                              "page": {
                                                "size": 4,
                                                "number": 0,
                                                  "totalElements": 16,
                                                                      "totalPages": 4
                                                                    }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de paginación inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Ejemplo de error",
                                    value = """
                                            {
                                              "timestamp": "2025-10-26T10:40:12",
                                              "status": 400,
                                              "error": "Parámetros inválidos",
                                              "message": "El número de página no puede ser negativo",
                                              "path": "/api/v1/history"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public Page<CalculationHistoryResponse> getHistory(
            @Parameter(
                    description = "Número de página a consultar (comienza desde 0).",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "El número de página no puede ser negativo")
            int page,

            @Parameter(
                    description = "Cantidad de registros a devolver por página.",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            @Min(value = 0, message = "El tamaño no puede ser negativo")
            @Max(value = 100, message = "El tamaño máximo permitido es 100")
            int size
    ) {
        return historyService.getLogs(page, size);
    }
}
