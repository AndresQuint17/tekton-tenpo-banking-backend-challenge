package com.tekton.challenge.backend.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ExternalPercentageService {

    private final Random random = new Random();

    public double getDynamicPercentage() {
        // Simular fallo aleatorio del servicio externo (30% de fallar)
        if (random.nextDouble() < 0.3) {
            throw new RuntimeException("Error al consultar el servicio externo");
        }

        // Simulamor que retorna un valor entre 5% y 15%
        return 5 + (10 * random.nextDouble());
    }
}
