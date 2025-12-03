package com.pkcs.chess.controller;

import com.pkcs.chess.model.dto.GameDto;
import com.pkcs.chess.service.ChessGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/${service.api.version}/chess")
@RequiredArgsConstructor
public class ChessController {
    private final ChessGameService chessGameService;


    @GetMapping("/games/{username}/{year}/{month}")
    public Flux<GameDto> getPlayerGames(@PathVariable String username,
                                              @PathVariable String year,
                                              @PathVariable String month) {
        return chessGameService.fetchPlayerGames(username, year, month);
    }
}
