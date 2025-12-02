package com.pkcs.chess.client;

import com.pkcs.chess.model.chesscom.GamesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChessComClient {
    private final WebClient chessComWebClient;

    public Mono<GamesResponse> getPlayerGames(String username, String year, String month) {
        return chessComWebClient.get()
                .uri("/player/{username}/games/{year}/{month}", username, year, month)
                .retrieve()
                .bodyToMono(GamesResponse.class);
    }
}
