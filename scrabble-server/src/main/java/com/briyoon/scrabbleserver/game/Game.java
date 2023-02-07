package com.briyoon.scrabbleserver.game;

import com.briyoon.scrabbleserver.dawg.Dawg;
import com.briyoon.scrabbleserver.documents.GameDoc;
import com.briyoon.scrabbleserver.board.Board;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.data.annotation.Id;

import static java.util.Map.entry;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Game {
    @Id
    private String gameID;
    private Board board;
    private Dawg dawg;
    private List<List<Character>> hands = new ArrayList<>();
    private List<Integer> scores = new ArrayList<>();
    private List<Character> drawPile = new ArrayList<>();
    private List<String> msgArray = new ArrayList<>();
    private List<String> jwts = new ArrayList<>();

    private static final Map<Character, Integer> drawList = Map.ofEntries(
        entry('*', 2),
        entry('e', 12),
        entry('a', 9),
        entry('i', 9),
        entry('o', 8),
        entry('n', 6),
        entry('r', 6),
        entry('t', 6),
        entry('l', 4),
        entry('s', 4),
        entry('u', 4),
        entry('d', 4),
        entry('g', 3),
        entry('b', 2),
        entry('c', 2),
        entry('m', 2),
        entry('p', 2),
        entry('f', 2),
        entry('h', 2),
        entry('v', 2),
        entry('w', 2),
        entry('y', 2),
        entry('k', 1),
        entry('j', 1),
        entry('x', 1),
        entry('q', 1),
        entry('z', 1)
    );

    // New Game Constructor
    public Game(String gameID) {
        this.gameID = gameID;
        this.board = new Board("scrabble-server/src/main/resources/boards/defaultBoard.txt"); // @TODO: custom boards
        this.dawg = new Dawg(); // @TODO: custom word lists
        this.hands = new ArrayList<List<Character>>() {
            // testing
            // {
            //     add(new ArrayList<Character>(List.of('c', 'h', 'e', 's')));
            //     add(new ArrayList<Character>(List.of('r',  'u', 'n')));
            // }
            {
                add(new ArrayList<Character>());
                add(new ArrayList<Character>());
            }
        };
        this.scores = new ArrayList<>(2) {
            {
                add(0);
                add(0);
            }
        };
        this.msgArray = new ArrayList<>(
            List.of(
                "Welcome to Scrabble!",
                "You are playing against a CPU.",
                "Drag a tile from your tray to a board to begin."
            )
        );

        // Create draw pile
        for (var letter : drawList.keySet()) {
            for (int i = 0; i < drawList.get(letter); i++) {
                drawPile.add(letter);
            }
        }

        // Populate hands
        for (int i = 0; i < 7; i++) {
            for (var hand : this.hands) {
                drawTiles(hand, 1);
            }
        }

        // Load dawg
        try {
            FileInputStream fin = new FileInputStream("scrabble-server/src/main/resources/dawgs/default.ser");
            ObjectInputStream ois = new ObjectInputStream(fin);

            this.dawg = (Dawg) ois.readObject();

            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Game(GameDoc gameDoc) {
        this.gameID = gameDoc.getId();
        this.board = gameDoc.getBoard();
        this.hands = gameDoc.getHands();
        this.scores = gameDoc.getScores();
        this.drawPile = gameDoc.getDrawpile();
        this.msgArray = gameDoc.getMsgArray();

        // Load dawg
        try {
            FileInputStream fin = new FileInputStream("scrabble-server/src/main/resources/dawgs/default.ser");
            ObjectInputStream ois = new ObjectInputStream(fin);

            this.dawg = (Dawg) ois.readObject();

            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load game constructor
    public Game(String gameID, Board board, List<List<Character>> hands, List<Integer> scores, List<Character> drawPile) {
        this.gameID = gameID;
        this.board = board;
        this.hands = hands;
        this.scores = scores;
        this.drawPile = drawPile;

        // Load dawg
        try {
            FileInputStream fin = new FileInputStream("scrabble-server/src/main/resources/dawgs/default.ser");
            ObjectInputStream ois = new ObjectInputStream(fin);

            this.dawg = (Dawg) ois.readObject();

            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawTiles(List<Character> hand, int drawCount) {
        for (int i = 0; i < drawCount; i++) {
            Random rand = new Random();
            int index = rand.nextInt(drawPile.size());
            hand.add(drawPile.get(index));
            drawPile.remove(index);
        }
    }

    public String getGameID() {
        return gameID;
    }

    public Board getBoard() {
        return board;
    }

    public List<List<Character>> getHands() {
        return hands;
    }

    public List<Character> getHands(int idx) {
        return hands.get(idx);
    }

    public List<Integer> getScores() {
        return scores;
    }

    public Integer getScores(int idx) {
        return scores.get(idx);
    }

    public Dawg getDawg() {
        return this.dawg;
    }

    public List<Character> getDrawPile() {
        return this.drawPile;
    }

    public List<String> getMsgArray() {
        return this.msgArray;
    }

    public void setBoard(String boardStr) {
        for (int i = 0; i < boardStr.length(); i++) {
            this.board.setTile(
                new ArrayList<Integer>(
                    Arrays.asList(
                        i / this.board.getSize(),
                        i % this.board.getSize()
                    )
                ),
                boardStr.charAt(i)
            );
        }
    }

    public void incrementScore(int index, int score) {
        this.scores.set(index, this.scores.get(index) + score);
    }

    public void incrementScores(List<Integer> scores) {
        for (int i = 0; i < this.scores.size(); i++) {
            this.scores.set(i, this.scores.get(i) + scores.get(i));
        }
    }

    public void appendMsgArray(String msg) {
        this.msgArray.add(msg);
    }

    public void appendMsgArray(String[] msgs) {
        for (String msg : msgs) {
            this.msgArray.add(msg);
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("gameID", this.gameID);

        JSONObject board = new JSONObject();
        board.put("size", this.board.getSize());
        board.put("tiles", this.board.getTiles());
        json.put("board", board);

        json.put("hand", this.getHands(1)); // currently 0 is cpu 1 is human, need to find a better way to assign a player an id

        Map<String, Integer> scores = new HashMap<>();
        scores.put("cpu", this.scores.get(0));
        scores.put("player", this.scores.get(1));
        json.put("scores", scores);

        json.put("msgArray", msgArray);

        return json;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}