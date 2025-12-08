package com.pkcs.chess.service;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import com.pkcs.chess.client.ChessComClient;
import com.pkcs.chess.client.DataServiceClient;
import com.pkcs.chess.model.chesscom.GameResponse;
import com.pkcs.chess.model.chesscom.GamesResponse;
import com.pkcs.chess.model.dto.GameDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ChessGameServiceTest {

    private ChessComClient chessComClient;
    private DataServiceClient dataServiceClient;
    private ChessPgnParserService chessPgnParserService;
    private ChessGameService chessGameService;
    private GameSavePublisher gameSavePublisher;

    @BeforeEach
    void setUp() {
        chessComClient = Mockito.mock(ChessComClient.class);
        dataServiceClient = Mockito.mock(DataServiceClient.class);
        chessPgnParserService = Mockito.mock(ChessPgnParserService.class);
        gameSavePublisher = Mockito.mock(GameSavePublisher.class);
        chessGameService = new ChessGameService(chessComClient, dataServiceClient, chessPgnParserService, gameSavePublisher);
        ReflectionTestUtils.setField(chessGameService, "concurrencyLimit", 10);
    }

    @Test
    void fetchPlayerGames_returnsEmptyFluxWhenNoGames() {
        String username = "testuser";
        String year = "2024";
        String month = "06";

        GamesResponse gamesResponse = Mockito.mock(GamesResponse.class);
        when(gamesResponse.getGames()).thenReturn(Collections.emptyList());
        when(chessComClient.getPlayerGames(username, year, month)).thenReturn(Mono.just(gamesResponse));

        Flux<GameDto> result = chessGameService.fetchPlayerGames(username, year, month);

        StepVerifier.create(result)
                .verifyComplete();

        Mockito.verify(chessComClient).getPlayerGames(username, year, month);
    }

    @Test
    void fetchPlayerGames_returnsGameDtos() throws Exception {
        String username = "testuser";
        String year = "2024";
        String month = "06";

        GameResponse gameResponse = new GameResponse();
        String pgnString = "[Event \"Live Chess\"]\n[Site \"Chess.com\"]\n[Date \"2024.06.27\"]\n[Round \"-\"]\n[White \"testuser\"]\n[Black \"opponent\"]\n[Result \"1-0\"]\n[WhiteElo \"1500\"]\n[BlackElo \"1450\"]\n[TimeControl \"600\"]\n[ECO \"C41\"]\n\n1. e4 e5 2. Nf3 d6 3. d4 exd4 4. Nxd4 Nf6 5. Nc3 Be7 1-0";
        gameResponse.setPgn(pgnString);

        GamesResponse gamesResponse = Mockito.mock(GamesResponse.class);
        when(gamesResponse.getGames()).thenReturn(Collections.singletonList(gameResponse));
        when(chessComClient.getPlayerGames(username, year, month)).thenReturn(Mono.just(gamesResponse));

        PgnHolder pgn = new PgnHolder(null);
        pgn.loadPgn(pgnString);
        Game parsedGame = pgn.getGames().get(0);

        when(chessPgnParserService.parsePgn(ArgumentMatchers.anyString())).thenReturn(parsedGame);

        when(gameSavePublisher.publishGameSaved(any(GameDto.class))).thenAnswer(invocation -> {
            GameDto gameDto = invocation.getArgument(0);
            return Flux.just(gameDto);
        });

        Flux<GameDto> result = chessGameService.fetchPlayerGames(username, year, month);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        Mockito.verify(chessComClient).getPlayerGames(username, year, month);
        Mockito.verify(chessPgnParserService).parsePgn(any(String.class));
        Mockito.verify(gameSavePublisher).publishGameSaved(any(GameDto.class));
    }
}
