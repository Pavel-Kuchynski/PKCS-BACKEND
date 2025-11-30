package com.pkcs.chess.service;

import com.pkcs.chess.client.ChessComClient;
import com.pkcs.chess.model.chesscom.GamesResponse;
import com.pkcs.chess.model.dto.GameDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class ChessGameServiceTest {

    private ChessComClient chessComClient;
    private ChessPgnParserService chessPgnParserService;
    private ChessGameService chessGameService;

    @BeforeEach
    void setUp() {
        chessComClient = mock(ChessComClient.class);
        chessPgnParserService = mock(ChessPgnParserService.class);
        chessGameService = new ChessGameService(chessComClient, chessPgnParserService);
    }

    @Test
    void fetchPlayerGames_returnsGameDtos() {
        String username = "testuser";
        String year = "2024";
        String month = "06";

        GamesResponse gamesResponse = mock(GamesResponse.class);
        when(gamesResponse.getGames()).thenReturn(Collections.emptyList());
        when(chessComClient.getPlayerGames(username, year, month)).thenReturn(Mono.just(gamesResponse));

        when(chessPgnParserService.parsePgn(anyString())).thenReturn(Collections.emptyList());

        Mono<List<GameDto>> result = chessGameService.fetchPlayerGames(username, year, month);

        StepVerifier.create(result)
                .expectNextMatches(List::isEmpty)
                .verifyComplete();

        verify(chessComClient).getPlayerGames(username, year, month);
    }
}
