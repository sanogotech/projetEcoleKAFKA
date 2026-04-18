package com.guce.declaration.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI declarationOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("GUCE CI 2.0 — declaration-service")
                        .description("Soumission de DAU et publication d'événements (Kafka ou mode simulation)")
                        .version("0.1.0"));
    }
}
