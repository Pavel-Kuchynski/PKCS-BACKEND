package com.pkcs.chess.client;

import com.pkcs.chess.model.chesscom.GamesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChessComClient {
    private final WebClient chessComWebClient;

    public Mono<GamesResponse> getPlayerGames(String username, String year, String month) {
        return chessComWebClient.get()
                .uri("/player/{username}/games/{year}/{month}", username, year, month)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("no-body")
                                .flatMap(body -> {
                                    log.warn("Chess.com 5xx error. user={}, year={}, month={}, body={}",
                                            username, year, month, body);
                                    return Mono.error(new IllegalStateException("Chess API temporary failure"));
                                })
                )
                .bodyToMono(GamesResponse.class)
                .onErrorResume(ex -> {
                    log.error("Chess.com unavailable for {}/{}/{} → returning empty list",
                            username, year, month, ex);
                    return Mono.just(new GamesResponse(List.of()));
                });
    }
}
