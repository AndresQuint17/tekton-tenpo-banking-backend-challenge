package com.tekton.challenge.backend.service;

import com.tekton.challenge.backend.dto.CalculationRequest;
import com.tekton.challenge.backend.dto.CalculationResponse;
import com.tekton.challenge.backend.exception.ExternalServiceUnavailableException;
import com.tekton.challenge.backend.util.Constants;
import org.springframework.stereotype.Service;

@Service
public class CalculationService {

    private final ExternalPercentageService externalService;
    private final PercentageCacheService cacheService;
    private final CalculationLogService logService;

    public CalculationService(ExternalPercentageService externalService,
                              PercentageCacheService cacheService,
                              CalculationLogService logService) {
        this.externalService = externalService;
        this.cacheService = cacheService;
        this.logService = logService;
    }

    public CalculationResponse calculate(CalculationRequest request) {
        double percentage;

        try {
            // 1. Intentar obtener el porcentaje del servicio externo
            percentage = externalService.getDynamicPercentage();

            // 2. Guardar (o reemplazar) el valor en la caché
            cacheService.savePercentage(percentage);

            double sum = request.num1() + request.num2();
            double result = sum + (sum * (percentage / 100));

            CalculationResponse response = new CalculationResponse(request.num1(), request.num2(), percentage, result);

            // 3. Registrar llamada exitosa (asíncrono)
            logService.saveLog(Constants.CALCULATE_ENDPOINT, request, response, true);

            return response;

        } catch (Exception ex) {
            // 4. Si falla, intentar usar el último valor en caché
            try {
                percentage = cacheService.getCachedPercentage()
                        .orElseThrow(() -> new ExternalServiceUnavailableException("El servicio externo no disponible y no hay valor en caché"));
                double sum = request.num1() + request.num2();
                double result = sum + (sum * (percentage / 100));
                CalculationResponse response = new CalculationResponse(request.num1(), request.num2(), percentage, result);

                logService.saveLog(Constants.CALCULATE_ENDPOINT, request, response, true);
                return response;
            } catch (Exception innerEx) {
                // 5.   Registrar error final
                logService.saveLog(Constants.CALCULATE_ENDPOINT, request, ex.getMessage(), false);
                throw innerEx;
            }
        }
    }
}
