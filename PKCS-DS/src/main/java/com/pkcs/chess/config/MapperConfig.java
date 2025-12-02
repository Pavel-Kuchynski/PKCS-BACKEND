package com.pkcs.chess.config;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.pkcs.chess.model.GameDocument;
import com.pkcs.chess.model.HalfMoveDocument;
import com.pkcs.chess.model.PlayerDocument;
import com.pkcs.chess.model.dto.GameDto;
import com.pkcs.chess.model.dto.HalfMoveDto;
import com.pkcs.chess.model.dto.PlayerDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MapperConfig {

    /**
     * Configures ModelMapper to map DTOs to Documents.
     */
    @Bean
    @Primary
    ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        // GameDto -> GameDocument
        mapper.addConverter(ctx -> {
            GameDto src = ctx.getSource();
            if (src == null) return null;
            return new GameDocument(
                    src.id(),
                    src.url(),
                    mapper.map(src.whitePlayer(), PlayerDocument.class),
                    mapper.map(src.blackPlayer(), PlayerDocument.class),
                    src.result(),
                    src.ecoCode(),
                    src.moves() != null ? src.moves().stream()
                            .map(m -> mapper.map(m, HalfMoveDocument.class))
                            .toList() : null
            );
        }, GameDto.class, GameDocument.class);

        // PlayerDto -> PlayerDocument
        mapper.addConverter(ctx -> {
            PlayerDto src = ctx.getSource();
            if (src == null) return null;
            return new PlayerDocument(src.username());
        }, PlayerDto.class, PlayerDocument.class);

        // HalfMoveDto -> HalfMoveDocument
        mapper.addConverter(ctx -> {
            var src = ctx.getSource();
            if (src == null) return null;
            return new HalfMoveDocument(
                    src.moveNumber(),
                    src.color() != null ? src.color().name() : null,
                    src.san(),
                    src.from() != null ? src.from().name() : null,
                    src.to() != null ? src.to().name() : null,
                    src.promotion() != null ? src.promotion().name() : null,
                    src.fenAfter()
            );
        }, HalfMoveDto.class, HalfMoveDocument.class);


        // GameDocument -> GameDto
        mapper.addConverter(ctx -> {
            GameDocument src = ctx.getSource();
            if (src == null) return null;
            return new GameDto(
                src.getId(),
                src.getUrl(),
                mapper.map(src.getWhitePlayer(), PlayerDto.class),
                mapper.map(src.getBlackPlayer(), PlayerDto.class),
                src.getResult(),
                src.getEcoCode(),
                src.getMoves() != null ? src.getMoves().stream()
                    .map(m -> mapper.map(m, HalfMoveDto.class))
                    .toList() : null
            );
        }, GameDocument.class, GameDto.class);

        // HalfMoveDocument -> HalfMoveDto
        mapper.addConverter(ctx -> {
            HalfMoveDocument src = ctx.getSource();
            if (src == null) return null;
            return new HalfMoveDto(
                src.getMoveNumber(),
                src.getColor() != null ? Side.valueOf(src.getColor()) : null,
                src.getSan(),
                src.getFrom() != null ? Square.valueOf(src.getFrom()) : null,
                src.getTo() != null ? Square.valueOf(src.getTo()) : null,
                src.getPromotion() != null ? Piece.valueOf(src.getPromotion()) : null,
                src.getFenAfter()
            );
        }, HalfMoveDocument.class, HalfMoveDto.class);

        // PlayerDocument -> PlayerDto
        mapper.addConverter(ctx -> {
            PlayerDocument src = ctx.getSource();
            if (src == null) return null;
            return new PlayerDto(src.getUsername());
        }, PlayerDocument.class, PlayerDto.class);

        return mapper;
    }
}
