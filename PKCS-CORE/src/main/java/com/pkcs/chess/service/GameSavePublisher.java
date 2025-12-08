package com.pkcs.chess.service;

import com.pkcs.chess.model.dto.GameDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@RequiredArgsConstructor
public class GameSavePublisher {
    private static final String TOPIC = "game.saved";
    private final KafkaTemplate<String, GameDto> kafkaTemplate;

    /**
     * Publishes a game saved event to the Kafka topic.
     *
     * @param gameDto the game data transfer object
     */
    public Flux<GameDto> publishGameSaved(GameDto gameDto) {
        log.debug("Publishing game saved event for game ID: {}", gameDto.id());
        return Mono.fromFuture(kafkaTemplate.send(TOPIC, gameDto.id(), gameDto))
                .map(result -> gameDto)
                .doOnSuccess(dto -> log.info("Game event published successfully: {}", dto.id()))
                .doOnError(ex -> log.error("Failed to publish game event: {}", gameDto.id(), ex))
                .flux();
    }
}
