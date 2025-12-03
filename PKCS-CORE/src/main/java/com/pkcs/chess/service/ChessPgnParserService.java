package com.pkcs.chess.service;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChessPgnParserService {

    /**
     * Parses a PGN string and returns a list of Game objects.
     *
     * @param pgn the PGN string to parse
     * @return a list of Game objects
     */
    public Game parsePgn(String pgn) {
        try {
            UUID uuid = UUID.randomUUID();
            File tempFile = File.createTempFile("tempGame_" + uuid, ".pgn");
            tempFile.deleteOnExit();

            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(pgn);
            }

            PgnHolder pgnHolder = new PgnHolder(tempFile.getAbsolutePath());
            pgnHolder.loadPgn();
            return pgnHolder.getGames().iterator().next();
        } catch (Exception e) {
            throw new RuntimeException("Error during PGN parsing ", e);
        }
    }
}
