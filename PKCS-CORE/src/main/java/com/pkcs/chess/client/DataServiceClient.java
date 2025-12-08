package com.pkcs.chess.client;

import com.pkcs.chess.model.dto.GameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DataServiceClient {
    private final WebClient pkcsDsWebClient;

    public Flux<GameDto> saveGame(GameDto gameDto) {
        return pkcsDsWebClient.post()
                .uri("/games")
                .bodyValue(gameDto)
                .retrieve()
                .bodyToFlux(GameDto.class);
    }

    public Mono<GameDto> getGameById(String gameId) {
        return pkcsDsWebClient.get()
                .uri("/games" + "/" + gameId)
                .retrieve()
                .bodyToMono(GameDto.class);
    }
}
