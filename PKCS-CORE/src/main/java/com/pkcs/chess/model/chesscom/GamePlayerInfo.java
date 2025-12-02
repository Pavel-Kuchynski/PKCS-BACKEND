package com.pkcs.chess.model.chesscom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GamePlayerInfo {
    private String username;
    private int rating;
    private String result;
    @JsonProperty(value = "@id")
    private String id;
    private String uuid;
}
