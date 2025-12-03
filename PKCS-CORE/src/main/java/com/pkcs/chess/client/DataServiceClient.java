package com.pkcs.chess.client;

import com.pkcs.chess.model.dto.GameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

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
}
