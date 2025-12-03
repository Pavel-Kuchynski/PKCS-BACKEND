package com.pkcs.chess.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ClientCredentialsTokenClient {

    private final WebClient webClient;
    private final String authHeader;
    private final String scope;

    public ClientCredentialsTokenClient(@Value("${cognito.token-uri}") String tokenUri,
                                        @Value("${cognito.client-id}") String clientId,
                                        @Value("${cognito.client-secret}") String clientSecret,
                                        @Value("${cognito.scope}") String scope) {
        this.scope = scope;
        this.webClient = WebClient.builder()
                .baseUrl(tokenUri)
                .build();

        String credentials = clientId + ":" + clientSecret;
        this.authHeader = "Basic " + new String(Base64.encode(credentials.getBytes(StandardCharsets.UTF_8)));
    }


    public Mono<String> getAccessToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("scope", scope);

        return webClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", authHeader)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .doOnEach(token-> log.debug("Obtained access token {}", token))
                .map(TokenResponse::access_token);
    }

    public record TokenResponse(String access_token, String token_type, Long expires_in) {}
}
