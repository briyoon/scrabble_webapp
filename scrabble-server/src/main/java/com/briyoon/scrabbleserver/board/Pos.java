package com.briyoon.scrabbleserver.board;

public class Pos {
    private int x;
    private int y;

    // Default constructor
    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Copy constructor
    public Pos(Pos pos) {
        this.x = pos.x;
        this.y = pos.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + ((Integer) this.x).hashCode();
        result = 31 * result + ((Integer) this.y).hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Pos)) {
            return false;
        }

        Pos pos2 = (Pos) obj;

        if (pos2.x != this.x || pos2.y != this.y) {
            return false;
        }

        return true;
    }
}
