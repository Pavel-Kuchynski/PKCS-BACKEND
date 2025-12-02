package com.pkcs.chess.controller;

import com.pkcs.chess.model.dto.GameDto;
import com.pkcs.chess.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/${service.api.version}/games")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping
    Mono<GameDto> createGame(@RequestBody GameDto gameDto) {
        return gameService.saveGame(gameDto);
    }

    @GetMapping("/{id}")
    public Mono<GameDto> getGame(@PathVariable String id) {
        return gameService.getGame(id);
    }
}
