package com.plazoleta.traceability.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/** Documentación OpenAPI. El JWT se obtiene en users-service (POST /api/login) y se pega con "Authorize". */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Plazoleta traceability API", version = "1.0",
        description = "Order status history (HU 17). Get the token from users-service (POST /api/login)."))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfiguration {
}
