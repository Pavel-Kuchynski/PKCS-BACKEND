package com.pkcs.chess.service;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.Move;
import com.pkcs.chess.client.ChessComClient;
import com.pkcs.chess.model.dto.GameDto;
import com.pkcs.chess.model.dto.HalfMoveDto;
import com.pkcs.chess.model.dto.PlayerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChessGameService {
    private final ChessComClient chessComClient;
    private final ChessPgnParserService chessPgnParserService;

    /**
     * Fetches the games of a player for a specific month.
     *
     * @param username the username of the player
     * @param year     the year
     * @param month    the month
     * @return a Mono emitting the GamesResponse containing the player's games
     */
    public Mono<List<GameDto>> fetchPlayerGames(String username, String year, String month) {
        log.info("Fetching games for user: {} for month: {}", username, String.format("%s/%s", year, month));
        return chessComClient.getPlayerGames(username, year, month)
                .map(response -> response.getGames().stream()
                        .flatMap(gameResp -> chessPgnParserService.parsePgn(gameResp.getPgn()).stream())
                        .toList())
                .map(games -> games.stream()
                        .map(this::mapToGameDto)
                        .toList());
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
