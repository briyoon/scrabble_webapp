package com.briyoon.scrabbleserver.board;

import java.io.File;
import java.util.*;

import org.springframework.data.annotation.PersistenceCreator;

public class Board {
    private int size;
    private List<Character> tiles = new ArrayList<>();

    public Board(String path) {
        List<String> input = new ArrayList<>();
        try {
            Scanner inScanner = new Scanner(new File(path));
            while (inScanner.hasNextLine()) {
                input.add(inScanner.nextLine());
            }
            inScanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.size = Integer.parseInt(input.get(0));
        // add tiles from input
        for (int i = 1; i < this.size + 1; i++) {
            for (String tile : input.get(i).split("")) {
                tiles.add(tile.charAt(0));
            }
        }
    }

    // Serialize constructor
    @PersistenceCreator
    public Board(int size, List<Character> tiles) {
        this.size = size;
        this.tiles = tiles;
    }

    // Copy constructor
    public Board(Board board) {
        this.size = board.size;
        for (int i = 0; i < board.size * board.size; i++) {
            this.tiles.add((char) i);
        }
        Collections.copy(this.tiles, board.tiles);
    }

    public int getSize() {
        return size;
    }

    public char getTile(List<Integer> pos) {
        return tiles.get(pos.get(0) * size + pos.get(1));
    }

    public List<Character> getTiles() {
        return tiles;
    }

    public void setTile(List<Integer> pos, char letter) {
        tiles.set(((pos.get(0)) * size) + (pos.get(1)), letter);
    }

    public List<List<Integer>> getAllPos() {
        List<List<Integer>> positions = new ArrayList<>();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                List<Integer> pos = new ArrayList<>(Arrays.asList(x, y));
                positions.add(pos);
            }
        }

        return positions;
    }

    public boolean isValidPos(List<Integer> pos) {
        return pos.get(0) >= 0 &&
               pos.get(0) < size &&
               pos.get(1) >= 0 &&
               pos.get(1) < size;
    }

    public boolean isEmpty(List<Integer> pos) {
        return isValidPos(pos) &&
               (tiles.get((pos.get(0) * size) + pos.get(1)).equals('.') ||
               tiles.get((pos.get(0) * size) + pos.get(1)).equals('{') ||
               tiles.get((pos.get(0) * size) + pos.get(1)).equals('[') ||
               tiles.get((pos.get(0) * size) + pos.get(1)).equals('}') ||
               tiles.get((pos.get(0) * size) + pos.get(1)).equals(']'));
    }

    public boolean isFilled(List<Integer> pos) {
        return isValidPos(pos) &&
               (!tiles.get((pos.get(0) * size) + pos.get(1)).equals('.') &&
               !tiles.get((pos.get(0) * size) + pos.get(1)).equals('{') &&
               !tiles.get((pos.get(0) * size) + pos.get(1)).equals('[') &&
               !tiles.get((pos.get(0) * size) + pos.get(1)).equals('}') &&
               !tiles.get((pos.get(0) * size) + pos.get(1)).equals(']'));
    }

    public String toString() {
        String boardString = "";
        for (var tile : tiles.toArray(new Character[tiles.size()])) {
            boardString += tile;
        }

        return boardString;
    }

    public String toStringFormatted() {
        String boardString = "";
        int tileCounter = 0;
        int rowCounter = 0;
        for (var tile : tiles.toArray(new Character[tiles.size()])) {
            tileCounter++;
            boardString += tile;
            if (tileCounter == size && rowCounter != size - 1) {
                tileCounter = 0;
                rowCounter++;
                boardString += "\n";
            }
        }

        return boardString;
    }
}
