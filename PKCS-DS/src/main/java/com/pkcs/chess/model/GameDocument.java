package com.pkcs.chess.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "games")
public class GameDocument {
    @Id
    private String id;
    private String url;
    private PlayerDocument whitePlayer;
    private PlayerDocument blackPlayer;
    private String result;
    private String ecoCode;
    private List<HalfMoveDocument> moves;
}
