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
public class GameResponse {
    private String url;
    private String pgn;
    @JsonProperty(value = "time_control")
    private String timeControl;
    @JsonProperty(value = "end_time")
    private String endTime;
    private boolean rated;
    private String tcn;
    private String uuid;
    @JsonProperty(value = "initial_setup")
    private String initialSetup;
    private GamePlayerInfo white;
    private GamePlayerInfo black;
    private String fen;
    @JsonProperty(value = "time_class")
    private String timeClass;
    private String rules;
    private String eco;
}
