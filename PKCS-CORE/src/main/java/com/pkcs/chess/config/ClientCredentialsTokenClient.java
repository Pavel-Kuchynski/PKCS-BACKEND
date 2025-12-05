package com.pkcs.chess.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class ClientCredentialsTokenClient {

    private final WebClient webClient;
    private final String tokenUri;
    private final String scope;

    private final AtomicReference<CachedToken> cachedToken = new AtomicReference<>();
    private final AtomicReference<Mono<CachedToken>> inFlightRequest = new AtomicReference<>();

    @Autowired
    public ClientCredentialsTokenClient(@Value("${cognito.token-uri}") String tokenUri,
                                        @Value("${cognito.client-id}") String clientId,
                                        @Value("${cognito.client-secret}") String clientSecret,
                                        @Value("${cognito.scope}") String scope) {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5));

        this.scope = scope;
        this.tokenUri = tokenUri;

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeaders(h -> h.setBasicAuth(clientId, clientSecret))
                .build();
    }

    // Constructor for testing purposes
    ClientCredentialsTokenClient(WebClient webClient, String scope) {
        this.webClient = webClient;
        this.scope = scope;
        this.tokenUri = "";
    }

    // Synchronized method to get access token
    public Mono<String> getAccessToken() {
        CachedToken token = cachedToken.get();

        // Return cached token if valid
        if (token != null && !token.isExpired()) {
            return Mono.just(token.token());
        }

        // Check for in-flight request
        Mono<CachedToken> existingRequest = inFlightRequest.get();
        if (existingRequest != null) {
            return existingRequest.map(CachedToken::token);
        }

        // Initiate new token request
        Mono<CachedToken> newRequest = requestNewToken()
                .doOnNext(newToken -> {
                    cachedToken.set(newToken);
                    log.info("New access token cached");
                })
                .doFinally(signal -> inFlightRequest.set(null))
                .cache();

        // Attempt to set the in-flight request
        if (inFlightRequest.compareAndSet(null, newRequest)) {
            return newRequest.map(CachedToken::token);
        }

        // Another request was set in the meantime, use that one
        return inFlightRequest.get().map(CachedToken::token);
    }

    private Mono<CachedToken> requestNewToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("scope", scope);

        return webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Token request failed: status={}, body={}",
                                            response.statusCode(), body);
                                    return Mono.error(
                                            new IllegalStateException("Failed to obtain access token"));
                                })
                )
                .bodyToMono(TokenResponse.class)
                .doOnNext(token ->
                        log.debug("Access token successfully obtained, expires in {} sec",
                                token.expiresIn()))
                .map(tr -> new CachedToken(
                        tr.accessToken(),
                        Instant.now().plusSeconds(tr.expiresIn() - 30)
                ));
    }

    public record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("expires_in") Long expiresIn
    ) {}

    private record CachedToken(String token, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
