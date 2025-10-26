package com.tekton.challenge.backend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bancaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Cálculo con Porcentaje Dinámico")
                        .description("Servicio REST para realizar cálculos con porcentaje dinámico, caché y registro de historial de llamadas al servicio.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Andrés David Quintero Caicedo")
                                .email("andres-quint@hotmail.com")
                                .url("https://andresquint17.github.io/cv-andres-quintero/"))
                        .license(new License()
                                .name("Licencia MIT")
                                .url("https://opensource.org/licenses/MIT"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Repositorio público del proyecto")
                        .url("https://github.com/AndresQuint17/tekton-tenpo-banking-backend-challenge"));
    }
}
