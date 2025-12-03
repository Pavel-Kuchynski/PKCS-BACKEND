package com.pkcs.chess.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${chesscom.api.base-url}")
    private String chessBaseUrl;

    @Bean(value = "chessComWebClient")
    public WebClient chessComWebClient(WebClient.Builder builder) {
        final int size = 16 * 1024 * 1024; // 16 MB
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        return builder
                .baseUrl(chessBaseUrl)
                .exchangeStrategies(strategies)
                .build();
    }

    @Bean(value = "pkcsDsWebClient")
    public WebClient pkcsDsWebClient(ClientCredentialsTokenClient tokenClient,
                                     @Value("${pkcs.api.pkcs-ds.base-url}") String pkcsDsBaseUrl) {
        return WebClient.builder()
                .baseUrl(pkcsDsBaseUrl)
                .filter((request, next) ->
                        tokenClient.getAccessToken()
                                .flatMap(token -> next.exchange(
                                        ClientRequest.from(request)
                                                .header("Authorization", "Bearer " + token)
                                                .build()
                                ))
                )
                .build();
    }
}
