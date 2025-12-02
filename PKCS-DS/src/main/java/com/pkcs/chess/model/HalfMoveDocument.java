package com.pkcs.chess.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HalfMoveDocument {
    private int moveNumber;
    private String color;
    private String san;
    private String from;
    private String to;
    private String promotion;
    private String fenAfter;
}
