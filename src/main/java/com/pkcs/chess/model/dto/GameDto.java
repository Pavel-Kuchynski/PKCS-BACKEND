package com.pkcs.chess.model.dto;

import java.util.List;

public record GameDto(String id,
                      String url,
                      PlayerDto whitePlayer,
                      PlayerDto blackPlayer,
                      String result,
                      String ecoCode,
                      List<HalfMoveDto> moves) {
}
