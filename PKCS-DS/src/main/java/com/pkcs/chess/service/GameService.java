package com.pkcs.chess.service;

import com.pkcs.chess.model.GameDocument;
import com.pkcs.chess.model.dto.GameDto;
import com.pkcs.chess.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {
    private final ModelMapper mapper;
    private final GameRepository repository;

    /**
     * Saves a game to the repository.
     *
     * @param game the game DTO to be saved
     * @return a Mono emitting the saved game DTO
     */
    public Mono<GameDto> saveGame(GameDto game) {
        return Mono.fromCallable(() -> mapper.map(game, GameDocument.class))
                .map(repository::save)
                .map(savedGame -> game);
    }

    /**
     * Retrieves a game by its ID.
     *
     * @param id the ID of the game to be retrieved
     * @return a Mono emitting the retrieved game DTO
     */
    public Mono<GameDto> getGame(String id) {
        return Mono.fromCallable(()-> repository.findById(id))
                .doOnEach(signal -> log.info("Fetching game with id: {}", id))
                .map(gameDocument -> mapper.map(gameDocument, GameDto.class));
    }
}
