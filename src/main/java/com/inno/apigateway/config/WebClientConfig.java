package com.inno.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final String INTERNAL_KEY_HEADER = "Internal-Key";

    @Bean
    public WebClient userWebClient(@Value("${application.config.user-service-url}") String baseUrl,
                                   @Value("${internal.api-key}") String internalKey) {

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(INTERNAL_KEY_HEADER, internalKey)
                .build();
    }

    @Bean
    public WebClient authWebClient(@Value("${application.config.auth-service-url}") String baseUrl,
                                   @Value("${internal.api-key}") String internalKey) {

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(INTERNAL_KEY_HEADER, internalKey)
                .build();
    }
}
