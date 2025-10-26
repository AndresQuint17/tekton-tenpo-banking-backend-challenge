package com.tekton.challenge.backend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    @DisplayName("Debe tener el endpoint de cálculo correctamente definido")
    void shouldHaveCorrectCalculateEndpoint() {
        assertEquals("/api/v1/calculator", Constants.CALCULATE_ENDPOINT,
                "El endpoint CALCULATE_ENDPOINT debería ser '/api/v1/calculator'");
    }

    @Test
    @DisplayName("La clase debe ser final y tener un constructor privado")
    void shouldBeFinalAndHavePrivateConstructor() throws Exception {
        // Verifica que la clase sea final
        assertTrue(java.lang.reflect.Modifier.isFinal(Constants.class.getModifiers()),
                "La clase Constants debería ser final para evitar herencia");

        // Verifica que tenga solo un constructor privado
        var constructors = Constants.class.getDeclaredConstructors();
        assertEquals(1, constructors.length, "Debe existir un único constructor");

        var constructor = constructors[0];
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()),
                "El constructor debe ser privado");

        // Forzamos acceso para cobertura completa
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
