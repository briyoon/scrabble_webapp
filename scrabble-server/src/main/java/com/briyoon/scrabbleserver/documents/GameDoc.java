package com.briyoon.scrabbleserver.documents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import com.briyoon.scrabbleserver.board.Board;
import com.briyoon.scrabbleserver.game.Game;

@Document(collection="games")
public class GameDoc {
    @Id
    private String id;

    private Board board;
    private List<List<Character>> hands;
    private List<Integer> scores;
    private List<Character> drawpile;
    private List<String> msgArray;
    private String dawgId;
    private List<String> jwts;

    @PersistenceCreator
    public GameDoc(
        String id,
        Board board,
        List<List<Character>> hands,
        List<Integer> scores,
        List<Character> drawpile,
        List<String> msgArray,
        String dawgId,
        List<String> jwts)
    {
        super();
        this.id = id;
        this.board = board;
        this.hands = hands;
        this.scores = scores;
        this.drawpile = drawpile;
        this.msgArray = msgArray;
        // this.dawgId = dawgId;
        // this.jwts = jwts;
    }

    public GameDoc(Game game) {
        super();
        this.id = game.getGameID();
        this.board = game.getBoard();
        this.hands = game.getHands();
        this.scores = game.getScores();
        this.drawpile = game.getDrawPile();
        this.msgArray = game.getMsgArray();
        // this.dawgId = null;
        // this.jwts = null;
    }

    public String getId() {
        return this.id;
    }

    public Board getBoard() {
        return this.board;
    }

    public List<List<Character>> getHands() {
        return this.hands;
    }

    public List<Integer> getScores() {
        return this.scores;
    }

    public List<Character> getDrawpile() {
        return this.drawpile;
    }

    public List<String> getMsgArray() {
        return this.msgArray;
    }

    public String getDawgId() {
        return this.dawgId;
    }

    public List<String> getJwts() {
        return this.jwts;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setHands(List<List<Character>> hands) {
        this.hands = hands;
    }

    public void setScores(List<Integer> scores) {
        this.scores = scores;
    }

    public void setDrawpile(List<Character> drawpile) {
        this.drawpile = drawpile;
    }

    public void setMsgArray(List<String> msgArray) {
        this.msgArray = msgArray;
    }

    public void setDawgId(String dawgId) {
        this.dawgId = dawgId;
    }

    public void setJwts(List<String> jwts) {
        this.jwts = jwts;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("gameID", this.id);

        JSONObject board = new JSONObject();
        board.put("size", this.board.getSize());
        board.put("tiles", this.board.getTiles());
        json.put("board", board);

        json.put("hand", this.hands.get(1)); // currently 0 is cpu 1 is human, need to find a better way to assign a player an id

        Map<String, Integer> scores = new HashMap<>();
        scores.put("cpu", this.scores.get(0));
        scores.put("player", this.scores.get(1));
        json.put("scores", scores);

        json.put("msgArray", msgArray);

        return json;
    }
}

