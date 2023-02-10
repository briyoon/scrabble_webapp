package com.briyoon.scrabbleserver.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.briyoon.scrabbleserver.board.Board;
import com.briyoon.scrabbleserver.board.Pos;
import com.briyoon.scrabbleserver.dawg.Dawg;
import com.briyoon.scrabbleserver.dawg.DawgNode;

public class Solver2 {
    private Board board;
    private Dawg dawg;
    private List<Character> rack;
    private int originalRackSize;

    private Boolean horizontal = false;
    private HashMap<Pos, List<Character>> crossCheckRes;
    private HashSet<Pos> anchors;
    private HashMap<String, ValidMove> moves = new HashMap<>();

    private static List<Character> allLetters = List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');
    private static HashMap<Character, Integer> letterMultiplierMap = new HashMap<>();
    static {
        letterMultiplierMap.put('(', 4);
        letterMultiplierMap.put('{', 3);
        letterMultiplierMap.put('[', 2);
    }
    private static HashMap<Character, Integer> wordMultiplierMap = new HashMap<>();
    static {
        wordMultiplierMap.put(')', 4);
        wordMultiplierMap.put('}', 3);
        wordMultiplierMap.put(']', 2);
    }

    private static HashMap<Character, Integer> letterValues = new HashMap<>();
    static {
        letterValues.put('*', 0);
        letterValues.put('e', 1);
        letterValues.put('a', 1);
        letterValues.put('i', 1);
        letterValues.put('o', 1);
        letterValues.put('n', 1);
        letterValues.put('r', 1);
        letterValues.put('t', 1);
        letterValues.put('l', 1);
        letterValues.put('s', 1);
        letterValues.put('u', 1);
        letterValues.put('d', 2);
        letterValues.put('g', 2);
        letterValues.put('b', 3);
        letterValues.put('c', 3);
        letterValues.put('m', 3);
        letterValues.put('p', 3);
        letterValues.put('f', 4);
        letterValues.put('h', 4);
        letterValues.put('v', 4);
        letterValues.put('w', 4);
        letterValues.put('y', 4);
        letterValues.put('k', 5);
        letterValues.put('j', 8);
        letterValues.put('x', 8);
        letterValues.put('q', 10);
        letterValues.put('z', 10);
    }

    public void swapDir() {
        this.horizontal = !this.horizontal;
    }

    public Pos before(Pos pos) {
        // horizontal
        if (this.horizontal) {
            return new Pos(pos.getX() - 1, pos.getY());
        }
        // vertical
        return new Pos(pos.getX(), pos.getY() - 1);
    }

    public Pos after(Pos pos) {
        // horizontal
        if (this.horizontal) {
            return new Pos(pos.getX() + 1, pos.getY());
        }
        // vertical
        return new Pos(pos.getX(), pos.getY() + 1);
    }

    public Pos beforeCross(Pos pos) {
        // horizontal
        if (this.horizontal) {
            return new Pos(pos.getX(), pos.getY() - 1);
        }
        // vertical
        return new Pos(pos.getX() - 1, pos.getY());
    }

    public Pos afterCross(Pos pos) {
        // horizontal
        if (this.horizontal) {
            return new Pos(pos.getX(), pos.getY() + 1);
        }
        // vertical
        return new Pos(pos.getX() + 1, pos.getY());
    }

    public HashSet<Pos> findAnchors() {
        HashSet<Pos> anchors = new HashSet<>();

        for (var pos : board.getAllPos()) {
            boolean empty = board.isEmpty(pos);
            boolean adjacentFilled = (board.isFilled(before(pos)) ||
                                        board.isFilled(after(pos)) ||
                                        board.isFilled(beforeCross(pos)) ||
                                        board.isFilled(afterCross(pos)));

            if (empty && adjacentFilled) {
                anchors.add(pos);
            }
        }

        // if no anchors found, first turn, add middle tile
        if (anchors.isEmpty()) {
            anchors.add(new Pos(board.getSize() / 2, board.getSize() / 2));
        }

        return anchors;
    }

    public HashMap<Pos, List<Character>> crossCheck() {
        HashMap<Pos, List<Character>> crossCheckRes = new HashMap<>();
        for (var pos : this.board.getAllPos()) {
            if (board.isFilled(pos)) {
                continue;
            }
            String beforePart = "";
            Pos scanPos = new Pos(pos);
            while (board.isFilled(beforeCross(scanPos))) {
                scanPos = beforeCross(scanPos);
                beforePart = board.getTile(scanPos) + beforePart;
            }
            String afterPart = "";
            scanPos = new Pos(pos);
            while (board.isFilled(afterCross(scanPos))) {
                scanPos = afterCross(scanPos);
                afterPart += board.getTile(scanPos);
            }
            List<Character> legalLetters = new ArrayList<>();
            if (beforePart.length() == 0 && afterPart.length() == 0) {
                legalLetters = allLetters;
            }
            else {
                for (var letter : allLetters) {
                    String wordFormed = beforePart + letter + afterPart;
                    if (dawg.find(wordFormed)) {
                        legalLetters.add(letter);
                    }
                }
            }
            crossCheckRes.putIfAbsent(pos, legalLetters);
        }
        return crossCheckRes;
    }

    public void legalMove(String word, Pos pos) {
        int playedWordScore = 0;
        int playedWordMultiplier = 1;
        int otherFormedWordScore = 0;
        Board tmpBoard = new Board(this.board);

        for (var letter : new StringBuilder(word).reverse().toString().toCharArray()) {
            // score played word
            char tile = this.board.getTile(pos);
            int tileScore = 0;
            int tileMultiplier = 1;
            if (letterMultiplierMap.containsKey(tile)) {
                tileScore = letterValues.get(letter) * letterMultiplierMap.get(tile);
            }
            else if (wordMultiplierMap.containsKey(tile)) {
                tileMultiplier *= wordMultiplierMap.get(tile);
                tileScore = letterValues.get(letter);
            }
            else if (Character.isLowerCase(letter)) {
                tileScore = letterValues.get(letter);
            }

            playedWordScore += tileScore;
            playedWordMultiplier *= tileMultiplier;
            tmpBoard.setTile(pos, letter);

            // check for cross formed words on anchor spots
            if (anchors.contains(pos) &&
               (tmpBoard.isFilled(beforeCross(pos)) ||
                tmpBoard.isFilled(afterCross(pos)))) {
                // current
                int crossCheckScore = tileScore;
                int crossCheckMultiplier = tileMultiplier;

                // before
                Pos crossCheckPos = beforeCross(pos);
                while (tmpBoard.isFilled(crossCheckPos)) {
                    tile = tmpBoard.getTile(crossCheckPos);
                    if (Character.isUpperCase(tile)) {
                        crossCheckPos = beforeCross(crossCheckPos);
                        continue;
                    }
                    crossCheckScore += letterValues.get(tile);
                    crossCheckPos = beforeCross(crossCheckPos);
                }
                // after
                crossCheckPos = afterCross(pos);
                while (tmpBoard.isFilled(crossCheckPos)) {
                    tile = tmpBoard.getTile(crossCheckPos);
                    if (Character.isUpperCase(tile)) {
                        crossCheckPos = afterCross(crossCheckPos);
                        continue;
                    }
                    crossCheckScore += letterValues.get(tile);
                    crossCheckPos = afterCross(crossCheckPos);
                }
                otherFormedWordScore += crossCheckScore * crossCheckMultiplier;
            }
            pos = before(pos);
        }

        // score for move
        int moveScore = (playedWordScore * playedWordMultiplier) + otherFormedWordScore;

        // bingo
        if (this.originalRackSize == 7 && this.rack.size() == 0) {
            moveScore += 50;
        }

        moves.put(tmpBoard.toString(), new ValidMove(word, moveScore, after(pos)));
    }

    public void extendBefore(String partialWord, DawgNode node, Pos anchor, int limit) {
        extendAfter(partialWord, node, anchor, false);
        if (limit > 0) {
            for (var letter : node.getChildren().keySet()) {
                if (this.rack.contains('*')) {
                    this.rack.remove(letter);
                    extendBefore(partialWord + Character.toUpperCase(letter), node.getChild(letter), anchor, limit - 1);
                    this.rack.add(letter);
                }
                if (this.rack.contains(letter)) {
                    this.rack.remove(letter);
                    extendBefore(partialWord + letter, node.getChild(letter), anchor, limit - 1);
                    this.rack.add(letter);
                }
            }
        }
    }

    public void extendAfter(String partialWord, DawgNode node, Pos pos, boolean anchor_filled) {
        if (!(this.board.isFilled(pos)) && node.isWord() && anchor_filled) {
            legalMove(partialWord, before(pos));
        }
        if (this.board.isValidPos(pos)) {
            if (this.board.isEmpty(pos)) {
                for (var letter : node.getChildren().keySet()) {
                    if (rack.contains('*') && crossCheckRes.containsKey(pos) && crossCheckRes.get(pos).contains(letter)) {
                        this.rack.remove(letter);
                        extendAfter(partialWord + Character.toUpperCase(letter), node.getChild(letter), after(pos), true);
                        this.rack.add(letter);
                    }
                    if (rack.contains(letter) && crossCheckRes.containsKey(pos) && crossCheckRes.get(pos).contains(letter)) {
                        this.rack.remove(letter);
                        extendAfter(partialWord + letter, node.getChild(letter), after(pos), true);
                        this.rack.add(letter);
                    }
                }
            }
            else {
                Character letter = board.getTile(pos);
                if (node.getChildren().containsKey(letter)) {
                    extendAfter(partialWord + letter, node.getChild(letter), after(pos), true);
                }
            }
        }
    }

    public HashMap<String, ValidMove> findAllMoves(Board board, Dawg dawg, List<Character> rack) {
        this.board = board;
        this.dawg = dawg;
        this.rack = rack;
        this.originalRackSize = this.rack.size();
        this.moves = new HashMap<String, ValidMove>();
        this.anchors = findAnchors();

        for (int i = 0; i < 2; i++) {
            this.crossCheckRes = crossCheck();

            for (var anchor : anchors) {
                Pos scanPos;
                String partialWord = "";
                DawgNode partialWordNode;

                if (this.board.isFilled(before(anchor))) {
                    scanPos = before(anchor);
                    partialWord += this.board.getTile(scanPos);
                    while (this.board.isFilled(before(scanPos))) {
                        scanPos = before(scanPos);
                        partialWord = this.board.getTile(scanPos) + partialWord;
                    }
                    partialWordNode = dawg.getNode(partialWord);
                    if (partialWordNode != null) {
                        extendAfter(partialWord, partialWordNode, anchor, false);
                    }
                }
                else {
                    int limit = 0;
                    scanPos = new Pos(anchor);
                    while (this.board.isEmpty(before(scanPos)) && !(anchors.contains(before(scanPos)))) {
                        limit++;
                        scanPos = before(scanPos);
                    }
                    extendBefore("", dawg.getRoot(), anchor, limit);
                }
            }
            swapDir();
        }
        return this.moves;
    }
}
