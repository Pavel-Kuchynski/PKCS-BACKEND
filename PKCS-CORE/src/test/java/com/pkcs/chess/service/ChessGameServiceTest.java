package com.pkcs.chess.service;

import com.pkcs.chess.client.ChessComClient;
import com.pkcs.chess.model.chesscom.GamesResponse;
import com.pkcs.chess.model.dto.GameDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

class ChessGameServiceTest {

    private ChessComClient chessComClient;
    private ChessPgnParserService chessPgnParserService;
    private ChessGameService chessGameService;

    @BeforeEach
    void setUp() {
        chessComClient = Mockito.mock(ChessComClient.class);
        chessPgnParserService = Mockito.mock(ChessPgnParserService.class);
        chessGameService = new ChessGameService(chessComClient, chessPgnParserService);
    }

    @Test
    void fetchPlayerGames_returnsGameDtos() {
        String username = "testuser";
        String year = "2024";
        String month = "06";

        GamesResponse gamesResponse = Mockito.mock(GamesResponse.class);
        Mockito.when(gamesResponse.getGames()).thenReturn(Collections.emptyList());
        Mockito.when(chessComClient.getPlayerGames(username, year, month)).thenReturn(Mono.just(gamesResponse));

        Mockito.when(chessPgnParserService.parsePgn(ArgumentMatchers.anyString())).thenReturn(Collections.emptyList());

        Mono<List<GameDto>> result = chessGameService.fetchPlayerGames(username, year, month);

        StepVerifier.create(result)
                .expectNextMatches(List::isEmpty)
                .verifyComplete();

        Mockito.verify(chessComClient).getPlayerGames(username, year, month);
    }
}
