package com.briyoon.scrabbleserver.board;

import java.io.InputStream;
import java.util.*;

import org.springframework.data.annotation.PersistenceCreator;

public class Board {
    private int size;
    private char[][] tiles;

    public Board(String path) {
        List<String> input = new ArrayList<>();
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(path);
            Scanner inScanner = new Scanner(is);
            while (inScanner.hasNextLine()) {
                input.add(inScanner.nextLine());
            }
            inScanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.size = Integer.parseInt(input.get(0));
        tiles = new char[this.size][this.size];
        // add tiles from input
        for (int i = 1; i < this.size + 1; i++) {
            int j = 0;
            for (String tile : input.get(i).split("")) {
                tiles[i-1][j] = tile.charAt(0);
                j++;
            }
        }
    }

    public Board(String boardString, int size) {
        this.size = size;
        this.tiles = new char[this.size][this.size];
        // add tiles from input
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                tiles[i][j] = boardString.charAt((i * this.size) + j);
            }
        }
    }

    // Serialize constructor
    @PersistenceCreator
    public Board(int size, char[][] tiles) {
        this.size = size;
        this.tiles = new char[this.size][this.size];
        // add tiles from input
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                this.tiles[i][j] = tiles[i][j];
            }
        }
    }

    // Copy constructor
    public Board(Board board) {
        this.size = board.size;
        this.tiles = new char[this.size][this.size];
        // add tiles from input
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                tiles[i][j] = board.getTile(new Pos(j, i));
            }
        }
    }

    public int getSize() {
        return size;
    }

    public char getTile(Pos pos) {
        return tiles[pos.getY()][pos.getX()];
    }

    public char[][] getTiles() {
        return tiles;
    }

    public void setTile(Pos pos, char letter) {
        tiles[pos.getY()][pos.getX()] = letter;
    }

    public List<Pos> getAllPos() {
        List<Pos> positions = new ArrayList<>();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                positions.add(new Pos(x, y));
            }
        }

        return positions;
    }

    public boolean isValidPos(Pos pos) {
        return pos.getX() >= 0 &&
               pos.getX() < size &&
               pos.getY() >= 0 &&
               pos.getY() < size;
    }

    public boolean isEmpty(Pos pos) {
        return
            isValidPos(pos) &&
            (
                tiles[pos.getY()][pos.getX()] == '.' ||
                tiles[pos.getY()][pos.getX()] == '{' ||
                tiles[pos.getY()][pos.getX()] == '[' ||
                tiles[pos.getY()][pos.getX()] == '}' ||
                tiles[pos.getY()][pos.getX()] == ']'
            );
    }

    public boolean isFilled(Pos pos) {
        return
            isValidPos(pos) &&
                (
                    tiles[pos.getY()][pos.getX()] != '.' &&
                    tiles[pos.getY()][pos.getX()] != '{' &&
                    tiles[pos.getY()][pos.getX()] != '[' &&
                    tiles[pos.getY()][pos.getX()] != '}' &&
                    tiles[pos.getY()][pos.getX()] != ']'
                );
    }

    public String toString() {
        String boardString = "";
        for (var row : tiles) {
            for (var tile : row) {
                boardString += tile;
            }
        }

        return boardString;
    }

    public String toStringFormatted() {
        String boardString = "";
        int tileCounter = 0;
        int rowCounter = 0;
        for (var row : tiles) {
            for (var tile : row) {
                tileCounter++;
                boardString += tile;
                if (tileCounter == size && rowCounter != size - 1) {
                    tileCounter = 0;
                    rowCounter++;
                    boardString += "\n";
                }
            }

        }

        return boardString;
    }

    public static void main(String[] args) {
        Pos pos1 = new Pos(1, 2);
        Pos pos2 = new Pos(2, 1);

        System.out.println(pos1.hashCode());
        System.out.println(pos2.hashCode());
    }
}