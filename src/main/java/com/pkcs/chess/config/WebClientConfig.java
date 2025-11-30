package com.pkcs.chess.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${chesscom.api.base-url}")
    private String chessBaseUrl;

    @Bean
    public WebClient chessComWebClient(WebClient.Builder builder) {
        return builder.baseUrl(chessBaseUrl).build();
    }
}
