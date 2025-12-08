package com.pkcs.chess.service;

import com.pkcs.chess.model.dto.GameDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameSaveListener {
    private final GameService gameService;

    @KafkaListener(topics = "game.saved", groupId = "pkcs-ds-group")
    public void listenGameSaved(GameDto gameDto) {
        log.info("Received game saved event for game ID: {}", gameDto.id());
        gameService.saveGame(gameDto)
                .doOnError(e -> log.error("Failed to save game: {}", gameDto.id(), e))
                .subscribe();
    }
}
