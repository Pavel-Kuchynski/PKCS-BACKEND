package com.pkcs.chess.config;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.pkcs.chess.model.GameDocument;
import com.pkcs.chess.model.dto.GameDto;
import com.pkcs.chess.model.dto.HalfMoveDto;
import com.pkcs.chess.model.dto.PlayerDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapperConfigTest {

    @Test
    void testGameDtoToGameDocumentMapping() {
        // Arrange
        PlayerDto white = new PlayerDto("whiteUser");
        PlayerDto black = new PlayerDto("blackUser");
        HalfMoveDto move1 = new HalfMoveDto(
                1, Side.WHITE, "e4", Square.E2, Square.E4, Piece.WHITE_PAWN, "fen1"
        );
        HalfMoveDto move2 = new HalfMoveDto(
                1, Side.BLACK, "e2", Square.E7, Square.E5, Piece.WHITE_PAWN, "fen2"
        );
        GameDto dto = new GameDto(
                "id1", "url1", white, black, "1-0", "C20", List.of(move1, move2)
        );
        ModelMapper mapper = new MapperConfig().modelMapper();

        // Act
        GameDocument doc = mapper.map(dto, GameDocument.class);

        // Assert
        assertEquals(dto.id(), doc.getId());
        assertEquals(dto.url(), doc.getUrl());
        assertEquals(dto.result(), doc.getResult());
        assertEquals(dto.ecoCode(), doc.getEcoCode());
        assertNotNull(doc.getWhitePlayer());
        assertEquals("whiteUser", doc.getWhitePlayer().getUsername());
        assertNotNull(doc.getBlackPlayer());
        assertEquals("blackUser", doc.getBlackPlayer().getUsername());
        assertNotNull(doc.getMoves());
        assertEquals(2, doc.getMoves().size());
        assertEquals(1, doc.getMoves().get(0).getMoveNumber());
        assertEquals("WHITE", doc.getMoves().get(0).getColor());
        assertEquals("e4", doc.getMoves().get(0).getSan());
        assertEquals("E2", doc.getMoves().get(0).getFrom());
        assertEquals("E4", doc.getMoves().get(0).getTo());
        assertEquals("WHITE_PAWN", doc.getMoves().get(0).getPromotion());
        assertEquals("fen1", doc.getMoves().get(0).getFenAfter());
    }

    @Test
    void testGameDocumentMappingToGameDto() {
        // Arrange
        com.pkcs.chess.model.PlayerDocument white = new com.pkcs.chess.model.PlayerDocument("whiteUser");
        com.pkcs.chess.model.PlayerDocument black = new com.pkcs.chess.model.PlayerDocument("blackUser");
        com.pkcs.chess.model.HalfMoveDocument move1 = new com.pkcs.chess.model.HalfMoveDocument(
                1, "WHITE", "e4", "E2", "E4", "WHITE_PAWN", "fen1"
        );
        com.pkcs.chess.model.HalfMoveDocument move2 = new com.pkcs.chess.model.HalfMoveDocument(
                1, "BLACK", "e2", "E7", "E5", "WHITE_PAWN", "fen2"
        );
        com.pkcs.chess.model.GameDocument doc = new com.pkcs.chess.model.GameDocument(
                "id1", "url1", white, black, "1-0", "C20", List.of(move1, move2)
        );
        ModelMapper mapper = new MapperConfig().modelMapper();

        // Act
        GameDto dto = mapper.map(doc, GameDto.class);

        // Assert
        assertEquals(doc.getId(), dto.id());
        assertEquals(doc.getUrl(), dto.url());
        assertEquals(doc.getResult(), dto.result());
        assertEquals(doc.getEcoCode(), dto.ecoCode());
        assertNotNull(dto.whitePlayer());
        assertEquals("whiteUser", dto.whitePlayer().username());
        assertNotNull(dto.blackPlayer());
        assertEquals("blackUser", dto.blackPlayer().username());
        assertNotNull(dto.moves());
        assertEquals(2, dto.moves().size());
        assertEquals(1, dto.moves().get(0).moveNumber());
        assertEquals(Side.WHITE, dto.moves().get(0).color());
        assertEquals("e4", dto.moves().get(0).san());
        assertEquals(Square.E2, dto.moves().get(0).from());
        assertEquals(Square.E4, dto.moves().get(0).to());
        assertEquals(Piece.WHITE_PAWN, dto.moves().get(0).promotion());
        assertEquals("fen1", dto.moves().get(0).fenAfter());
    }
}
