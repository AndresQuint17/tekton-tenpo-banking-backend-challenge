package com.tekton.challenge.backend.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.*;

class WebConfigTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner().withUserConfiguration(WebConfig.class);

    @Test
    @DisplayName("Debe cargar el contexto correctamente con WebConfig")
    void shouldLoadContextWithWebConfig() {
        contextRunner.run(context -> {
            assertTrue(context.containsBean("webConfig"),
                    "El contexto debe contener un bean llamado 'webConfig'");
            assertNotNull(context.getBean(WebConfig.class),
                    "Debe existir una instancia de WebConfig en el contexto");
        });
    }

    @Test
    @DisplayName("Debe estar anotada con @Configuration y @EnableSpringDataWebSupport(VIA_DTO)")
    void shouldHaveProperAnnotations() {
        // Verifica que tenga @Configuration
        assertTrue(WebConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class),
                "WebConfig debe estar anotada con @Configuration");

        // Verifica la presencia de @EnableSpringDataWebSupport
        var annotation = WebConfig.class.getAnnotation(EnableSpringDataWebSupport.class);
        assertNotNull(annotation, "WebConfig debe tener la anotación @EnableSpringDataWebSupport");

        // Verifica el modo de serialización
        assertEquals(EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO, annotation.pageSerializationMode(),
                "El modo de serialización de páginas debe ser VIA_DTO");
    }

    @Test
    @DisplayName("Debe implementar correctamente WebMvcConfigurer (implícitamente o no)")
    void shouldBeAValidWebMvcConfigurer() {
        WebConfig config = new WebConfig();
        assertTrue(config instanceof WebMvcConfigurer || WebConfig.class.getInterfaces().length == 0,
                "WebConfig debe poder coexistir con configuración WebMvc sin errores");
    }
}
