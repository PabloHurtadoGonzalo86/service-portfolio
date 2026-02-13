package com.example.serviceportfolio.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Service Portfolio API")
                .description("REST API that analyzes GitHub repositories and generates professional developer portfolios using AI")
                .version("1.0.0")
                .contact(
                    Contact()
                        .name("Pablo Hurtado Gonzalo")
                        .url("https://github.com/PabloHurtadoGonzalo86")
                )
                .license(
                    License()
                        .name("MIT")
                        .url("https://opensource.org/licenses/MIT")
                )
        )
        .servers(
            listOf(
                Server().url("https://serviceportfolioapi.pablohgdev.com").description("Production"),
                Server().url("https://serviceportfolioapi-dev.pablohgdev.com").description("Development"),
                Server().url("http://localhost:8080").description("Local")
            )
        )
}
