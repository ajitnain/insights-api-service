package com.example.insights.config;

import com.example.insights.security.DemoTokens;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@Configuration
public class OpenApiConfig {

    private static final String BEARER = "bearerAuth";

    @Bean
    public OpenAPI insightsOpenApi(DemoTokens demoTokens) {
        String tokens = tokenList(demoTokens);

        return new OpenAPI()
                .info(new Info()
                        .title("Retail Media Insights API")
                        .version("v1")
                        .description("Every endpoint needs a bearer token. Click **Authorize** and paste one of these:\n\n"
                                + tokens))
                .addSecurityItem(new SecurityRequirement().addList(BEARER))
                .components(new Components().addSecuritySchemes(BEARER,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .description("Paste one of the demo tokens:\n\n" + tokens)));
    }

    // Keep Swagger's sample tokens in sync with application.yaml.
    private static String tokenList(DemoTokens demoTokens) {
        return demoTokens.demoTokens().stream()
                .map(token -> "- `" + token.token() + "` &mdash; " + token.tenant())
                .collect(Collectors.joining("\n"));
    }
}
