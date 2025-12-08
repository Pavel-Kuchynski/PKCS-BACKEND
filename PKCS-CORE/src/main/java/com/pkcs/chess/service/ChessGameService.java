package com.pkcs.chess.service;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.Move;
import com.pkcs.chess.client.ChessComClient;
import com.pkcs.chess.client.DataServiceClient;
import com.pkcs.chess.model.chesscom.GamesResponse;
import com.pkcs.chess.model.dto.GameDto;
import com.pkcs.chess.model.dto.HalfMoveDto;
import com.pkcs.chess.model.dto.PlayerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChessGameService {
    private final ChessComClient chessComClient;
    private final DataServiceClient dataServiceClient;
    private final ChessPgnParserService chessPgnParserService;
    private final GameSavePublisher gameSavePublisher;

    @Value("${pkcs.concurrent-requests.max: 50}")
    private Integer concurrencyLimit;

    /**
     * Fetches the games of a player for a specific month.
     *
     * @param username the username of the player
     * @param year     the year
     * @param month    the month
     * @return a Mono emitting the GamesResponse containing the player's games
     */
    public Flux<GameDto> fetchPlayerGames(String username, String year, String month) {
        log.debug("Fetching games for user: {} for month: {}/{}", username, year, month);
        return chessComClient.getPlayerGames(username, year, month)
                .onErrorResume(ex -> {
                    log.error("Chess.com unavailable for {}/{}/{}. Returning empty result.",
                            username, year, month, ex);
                    return Mono.just(new GamesResponse(List.of()));
                })
                .flatMapMany(response ->
                        Flux.fromIterable(
                                Optional.ofNullable(response.getGames())
                                        .orElse(List.of())
                        )
                )
                .flatMap(gameResp ->
                                Mono.fromCallable(() -> chessPgnParserService.parsePgn(gameResp.getPgn()))
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .onErrorResume(ex -> {
                                            log.warn("Invalid PGN skipped. Game={}", gameResp.getUrl(), ex);
                                            return Mono.empty();
                                        }),
                        4
                )
                .map(this::mapToGameDto)
                .doOnNext(game -> log.debug("Saving game with id: {}", game.id()))
                .flatMap(gameSavePublisher::publishGameSaved, concurrencyLimit)
                .onErrorContinue((ex, obj) ->
                        log.error("Unexpected error in pipeline. Skipping element: {}", obj, ex)
                );
    }

    public Mono<GameDto> getGame(String id) {
        return dataServiceClient.getGameById(id);
    }

    private GameDto mapToGameDto(Game game) {
        PlayerDto whitePlayer = new PlayerDto(game.getWhitePlayer().getName());
        PlayerDto blackPlayer = new PlayerDto(game.getBlackPlayer().getName());

        List<HalfMoveDto> moves = new ArrayList<>();
        int moveNum = 1;

        List<Move> halfMoves = game.getHalfMoves();

        Board board = new Board();
        board.loadFromFen(game.getHalfMoves().getStartFen());

        Iterator<Move> iterator = halfMoves.iterator();


        while (iterator.hasNext()) {
            Move move = iterator.next();
            board.doMove(move);

            HalfMoveDto halfMoveDto = new HalfMoveDto(moveNum,
                    Side.WHITE,
                    move.getSan(),
                    move.getFrom(),
                    move.getTo(),
                    move.getPromotion(),
                    board.getFen());

            moves.add(halfMoveDto);
            if (iterator.hasNext()) {
                move = iterator.next();
                board.doMove(move);

                halfMoveDto = new HalfMoveDto(moveNum,
                        Side.BLACK,
                        move.getSan(),
                        move.getFrom(),
                        move.getTo(),
                        move.getPromotion(),
                        board.getFen());

                moves.add(halfMoveDto);
            }
            moveNum++;
        }

        return new GameDto(
                game.getGameId(),
                game.getEco(),
                whitePlayer,
                blackPlayer,
                game.getResult().toString(),
                game.getEco(),
                moves
        );
    }
}
