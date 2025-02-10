package com.scrapad.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI scrapAdOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ScrapAd Financing API")
                .description("API for managing offers and financing in ScrapAd marketplace. " +
                    "This API allows sellers to receive financing based on their organization's characteristics " +
                    "and transaction history.\n\n" +
                    "## Financing Rules\n" +
                    "- Organizations from Spain/France with >10,000 ads and >1 year: Bank financing (5% fee)\n" +
                    "- Organizations from other countries with >10,000 ads and >1 year: Fintech financing (7% fee)\n\n" +
                    "## Common Use Cases\n" +
                    "1. Create an offer for an ad\n" +
                    "2. Check pending offers\n" +
                    "3. Request financing if eligible\n" +
                    "4. Accept offer with or without financing")
                .version("1.0.0")
                .contact(new Contact()
                    .name("ScrapAd Team")
                    .email("support@scrapad.com")
                    .url("https://scrapad.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Development server"),
                new Server()
                    .url("https://api.scrapad.com")
                    .description("Production server")));
    }
} 