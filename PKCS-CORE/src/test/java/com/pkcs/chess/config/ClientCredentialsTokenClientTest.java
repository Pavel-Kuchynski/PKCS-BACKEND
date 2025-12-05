package com.pkcs.chess.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for ClientCredentialsTokenClient to verify concurrent access token requests.
 */
class ClientCredentialsTokenClientTest {

    @Test
    void testConcurrentAccessTokenRequests() throws InterruptedException {
        AtomicInteger requestCount = new AtomicInteger(0);

        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);
        when(exchangeFunction.exchange(any())).thenAnswer(invocation -> {
            requestCount.incrementAndGet();
            ClientResponse response = ClientResponse.create(HttpStatus.OK)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"access_token\":\"token-123\",\"token_type\":\"Bearer\",\"expires_in\":60}")
                    .build();
            return Mono.just(response);
        });

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        ClientCredentialsTokenClient client = new ClientCredentialsTokenClient(
                webClient,
                "scope"
        );

        int threads = 10;
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                StepVerifier.create(client.getAccessToken())
                        .expectNext("token-123")
                        .verifyComplete();
                latch.countDown();
            }).start();
        }

        latch.await();

        assert (requestCount.get() == 1);
    }
}
