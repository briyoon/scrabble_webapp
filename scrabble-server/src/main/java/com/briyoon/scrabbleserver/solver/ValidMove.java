package com.briyoon.scrabbleserver.solver;

import com.briyoon.scrabbleserver.board.Pos;

public class ValidMove {
    private String word;
    private int score;
    private Pos pos;

    public ValidMove(String word, int score, Pos pos) {
        this.word = word;
        this.score = score;
        this.pos = pos;
    }

    public String getWord() {
        return this.word;
    }

    public int getScore() {
        return this.score;
    }

    public Pos getPos() {
        return this.pos;
    }

    @Override
    public String toString() {
        return this.word + " for " + this.score;
    }
}
