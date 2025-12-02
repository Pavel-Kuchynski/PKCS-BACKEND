package com.pkcs.chess.model.dto;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;

public record HalfMoveDto(int moveNumber,
                          Side color,       // WHITE or BLACK
                          String san,
                          Square from,
                          Square to,
                          Piece promotion,
                          String fenAfter) {
}
