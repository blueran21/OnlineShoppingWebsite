package com.codebase.itemservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for the item service. Adds basic
 * documentation metadata which appears in the Swagger UI.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Item Service API",
                version = "v1",
                description = "API documentation for the Item Service, responsible for managing product metadata and inventory levels."
        )
)

public class OpenApiConfig {
}
