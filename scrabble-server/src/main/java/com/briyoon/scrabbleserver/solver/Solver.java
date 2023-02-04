package com.briyoon.scrabbleserver.solver;

import java.util.*;

import com.briyoon.scrabbleserver.board.Board;
import com.briyoon.scrabbleserver.dawg.Dawg;
import com.briyoon.scrabbleserver.dawg.DawgNode;
import com.briyoon.scrabbleserver.game.Game;

import static java.util.Map.entry;
import java.lang.StringBuilder;

public class Solver {
    private static final boolean DEBUG = false;

    Game game;
    Dawg dawg;
    Board board;
    List<Character> tray;
    String ogTray;
    String dir = "down";

    List<Character> allLetters = List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');
    HashMap<List<Integer>, List<Character>> crossCheckRes = null;

    String highestWord = "";
    int highestScore = 0;
    Board highestBoard = null;
    String highestPos = null;
    String highestDir = null;
    List<List<String>> wordList = new ArrayList<>();

    int initialTraySize;

    Map<Character, Integer> letterValues = Map.ofEntries(
        entry('*', 0),
        entry('e', 1),
        entry('a', 1),
        entry('i', 1),
        entry('o', 1),
        entry('n', 1),
        entry('r', 1),
        entry('t', 1),
        entry('l', 1),
        entry('s', 1),
        entry('u', 1),
        entry('d', 2),
        entry('g', 2),
        entry('b', 3),
        entry('c', 3),
        entry('m', 3),
        entry('p', 3),
        entry('f', 4),
        entry('h', 4),
        entry('v', 4),
        entry('w', 4),
        entry('y', 4),
        entry('k', 5),
        entry('j', 8),
        entry('x', 8),
        entry('q', 10),
        entry('z', 10)
    );

    public Solver(Game game) {
        this.game = game;
        this.dawg = game.getDawg();
        this.board = game.getBoard();
    }

    private void swapDir() {
        if (this.dir.equals("across")) {this.dir = "down";}
        else {this.dir = "across";}
        return;
    }

    public List<Integer> before(List<Integer> pos) {
        List<Integer> retVal = new ArrayList<>(Arrays.asList(pos.get(0), pos.get(1)));
        if (this.dir.equals("across")) {
            retVal.set(1, retVal.get(1) - 1);
            return retVal;
        }
        else {
            retVal.set(0, retVal.get(0) - 1);
            return retVal;
        }
    }

    public List<Integer> after(List<Integer> pos) {
        List<Integer> retVal = new ArrayList<>(Arrays.asList(pos.get(0), pos.get(1)));
        if (this.dir.equals("across")) {
            retVal.set(1, retVal.get(1) + 1);
            return retVal;
        }
        else {
            retVal.set(0, retVal.get(0) + 1);
            return retVal;
        }
    }

    public List<Integer> beforeCross(List<Integer> pos) {
        List<Integer> retVal = new ArrayList<>(Arrays.asList(pos.get(0), pos.get(1)));
        if (this.dir.equals("across")) {
            retVal.set(0, retVal.get(0) - 1);
            return retVal;
        }
        else {
            retVal.set(1, retVal.get(1) - 1);
            return retVal;
        }
    }

    public List<Integer> afterCross(List<Integer> pos) {
        List<Integer> retVal = new ArrayList<>(Arrays.asList(pos.get(0), pos.get(1)));
        if (this.dir.equals("across")) {
            retVal.set(0, retVal.get(0) + 1);
            return retVal;
        }
        else {
            retVal.set(1, retVal.get(1) + 1);
            return retVal;
        }
    }

    public HashMap<List<Integer>, List<Character>> crossCheck() {
        HashMap<List<Integer>, List<Character>> results = new HashMap<>();
        for (var pos : board.getAllPos()) {
            if (board.isFilled(pos)) {
                continue;
            }
            String lettersBefore = "";
            List<Integer> scanPos = new ArrayList<>(Arrays.asList(pos.get(0), pos.get(1)));;
            while (board.isFilled(beforeCross(scanPos))) {
                scanPos = beforeCross(scanPos);
                lettersBefore = board.getTile(scanPos) + lettersBefore;
            }
            String lettersAfter = "";
            scanPos = pos;
            while (board.isFilled(afterCross(scanPos))) {
                scanPos = afterCross(scanPos);
                lettersAfter += board.getTile(scanPos);
            }
            List<Character> legalLetters = new ArrayList<>();
            if (lettersBefore.length() == 0 && lettersAfter.length() == 0) {
                legalLetters = allLetters;
            }
            else {
                for (var letter : allLetters) {
                    String wordFormed = lettersBefore + letter + lettersAfter;
                    if (dawg.find(wordFormed)) {
                        legalLetters.add(letter);
                    }
                }
            }
            results.putIfAbsent(pos, legalLetters);
        }
        return results;
    }

    public List<List<Integer>> findAnchors() {
        List<List<Integer>> anchors = new ArrayList<>();
        for (var pos : board.getAllPos()) {
            boolean empty = board.isEmpty(pos);
            boolean adjacentFilled = (board.isFilled(before(pos)) ||
                                        board.isFilled(after(pos)) ||
                                        board.isFilled(beforeCross(pos)) ||
                                        board.isFilled(afterCross(pos)));

            if (empty && adjacentFilled) {anchors.add(pos);}
        }
        if (anchors.isEmpty()) {
            anchors.add(List.of(board.getSize() / 2, board.getSize() / 2));
        }
        return anchors;
    }

    public void legalMove(String word, List<Integer> lastPos) {
        int score1 = 0;
        int score2 = 0;
        int wordMultiplier1 = 1;
        int bingo = 0;
        if (initialTraySize == 7 && tray.isEmpty()) {bingo = 50;}

        List<Integer> pos = lastPos;
        List<List<Integer>> wordPos = new ArrayList<>();
        // reconstruct score
        // score for played word
        for (var letter : new StringBuilder(word).reverse().toString().toCharArray()) {
            switch (board.getTile(pos)) {
                case ')': // quad word
                    if (!Character.isUpperCase(letter)) {score1 += letterValues.get(letter);}
                    wordMultiplier1 *= 4;
                    break;
                case '}':
                    if (!Character.isUpperCase(letter)) {score1 += letterValues.get(letter);}
                    wordMultiplier1 *= 3;
                    break;
                case ']':
                    if (!Character.isUpperCase(letter)) {score1 += letterValues.get(letter);}
                    wordMultiplier1 *= 2;
                    break;
                case '(': // quad letter
                    if (!Character.isUpperCase(letter)) {score1 += letterValues.get(letter) * 4;}
                    break;
                case '{':
                    if (!Character.isUpperCase(letter)) {score1 += letterValues.get(letter) * 3;}
                    break;
                case '[':
                    if (!Character.isUpperCase(letter)) {score1 += letterValues.get(letter) * 2;}
                    break;
                default:
                    if (!Character.isUpperCase(letter)) {score1 += letterValues.get(letter);}
                    break;
            }
            wordPos.add(pos);
            pos = before(pos);
        }

        pos = after(pos);
        int firstPos = ((pos.get(0)) * board.getSize()) + (pos.get(1));

        // compute board as if word was played
        Board tmpBoard = new Board(board);
        pos = lastPos;
        int wordIndex = word.length() - 1;
        while (wordIndex >= 0) {
            tmpBoard.setTile(pos, word.charAt(wordIndex));
            wordIndex--;
            pos = before(pos);
        }

        // add score for any words created thru cross references
        swapDir();
        for (var wPos : wordPos) {
            int score2tmp = 0;
            int wordMultiplier2tmp = 1;
            if (this.crossCheckRes.get(wPos) != null && !this.crossCheckRes.get(wPos).containsAll(allLetters)) {
                if (DEBUG) System.out.println("[Solver] Crosschecking for word: " + word);
                // if the tile before is empty, then read tiles after
                if (tmpBoard.isEmpty(before(wPos)) || !tmpBoard.isValidPos(before(wPos))) {
                    while (tmpBoard.isFilled(wPos)) {
                        char letter = tmpBoard.getTile(wPos);
                        if (!Character.isUpperCase(letter)) {
                            switch (board.getTile(wPos)) {
                                case ')': // quad word
                                    score2tmp += letterValues.get(letter);
                                    wordMultiplier2tmp *= 4;
                                    break;
                                case '}':
                                    score2tmp += letterValues.get(letter);
                                    wordMultiplier2tmp *= 3;
                                    break;
                                case ']':
                                    score2tmp += letterValues.get(letter);
                                    wordMultiplier2tmp *= 2;
                                    break;
                                case '(': // quad letter
                                    score2tmp += letterValues.get(letter) * 4;
                                    break;
                                case '{':
                                    score2tmp += letterValues.get(letter) * 3;
                                    break;
                                case '[':
                                    score2tmp += letterValues.get(letter) * 2;
                                    break;
                                default:
                                    score2tmp += letterValues.get(letter);
                                    break;
                            }
                        }
                        wPos = after(wPos);
                    }
                    score2 += score2tmp * wordMultiplier2tmp;
                }
                // if the tile after is empty, then read tiles before
                else if (tmpBoard.isEmpty(after(wPos)) || !tmpBoard.isValidPos(after(wPos))) {
                    while (tmpBoard.isFilled(wPos)) {
                        char letter = tmpBoard.getTile(wPos);
                        if (!Character.isUpperCase(letter)) {
                            switch (board.getTile(wPos)) {
                                case ')': // quad word
                                score2tmp += letterValues.get(letter);
                                    wordMultiplier2tmp *= 4;
                                    break;
                                case '}':
                                    score2tmp += letterValues.get(letter);
                                    wordMultiplier2tmp *= 3;
                                    break;
                                case ']':
                                    score2tmp += letterValues.get(letter);
                                    wordMultiplier2tmp *= 2;
                                    break;
                                case '(': // quad letter
                                    score2tmp += letterValues.get(letter) * 4;
                                    break;
                                case '{':
                                    score2tmp += letterValues.get(letter) * 3;
                                    break;
                                case '[':
                                    score2tmp += letterValues.get(letter) * 2;
                                    break;
                                default:
                                    score2tmp += letterValues.get(letter);
                                    break;
                            }
                        }
                        wPos = before(wPos);
                    }
                    score2 += score2tmp * wordMultiplier2tmp;
                }
            }
        }
        swapDir();

        int score = ((score1 * wordMultiplier1) + (score2) + bingo);
        wordList.add(List.of(tmpBoard.toString().toUpperCase(), word.toUpperCase(), "" + score, this.dir, "" + firstPos)); //@TESTING
        if (DEBUG) System.out.println("[Solver] score after multiplier and crosscheck for word: " + word + " " + score);

        if (score > highestScore) {
            highestScore = score;
            highestWord = word;
            highestBoard = new Board(tmpBoard);
            highestPos = "" + firstPos;
            highestDir = new String(this.dir);
        }
        else if (DEBUG) {
            if (score == highestScore)
                System.out.println("[Solver] score match (" + score + ") /w word: " + word);
        }
    }

    public void beforePart(String partialWord, DawgNode currentNode, List<Integer> anchor, int limit) {
        extendAfter(partialWord, currentNode, anchor, false);
        if (limit > 0) {
            for (var letter : currentNode.getChildren().keySet()) {
                if (tray.contains('*')) {
                    tray.remove(Character.valueOf('*'));
                    beforePart(partialWord + Character.toUpperCase(letter), currentNode.getChild(letter), anchor, limit - 1);
                    tray.add('*');
                }
                if (tray.contains(letter)) {
                    tray.remove(Character.valueOf(letter));
                    beforePart(partialWord + letter, currentNode.getChild(letter), anchor, limit - 1);
                    tray.add(letter);
                }
            }
        }
    }

    public void extendAfter(String partialWord, DawgNode currentNode, List<Integer> nextPos, boolean anchorFilled) {
        if (!board.isFilled(nextPos) && currentNode.getIsWord() && anchorFilled) {
            legalMove(partialWord, before(nextPos));
        }
        if (board.isValidPos(nextPos)) {
            if (board.isEmpty(nextPos)) {
                for (var letter : currentNode.getChildren().keySet()) {
                    if (tray.contains('*') && this.crossCheckRes.get(nextPos) != null && this.crossCheckRes.get(nextPos).contains(letter)) {
                        tray.remove(Character.valueOf('*'));
                        extendAfter(partialWord + Character.toUpperCase(letter), currentNode.getChild(letter), after(nextPos), true);
                        tray.add('*');
                    }
                    if (tray.contains(letter) && this.crossCheckRes.get(nextPos) != null && this.crossCheckRes.get(nextPos).contains(letter)) {
                        tray.remove(Character.valueOf(letter));
                        extendAfter(partialWord + letter, currentNode.getChild(letter), after(nextPos), true);
                        tray.add(letter);
                    }

                }
            }
            else {
                char existingLetter = board.getTile(nextPos);
                if (currentNode.getChildren().containsKey(existingLetter)) {
                    extendAfter(partialWord + existingLetter, currentNode.getChild(existingLetter), after(nextPos), true);
                }
            }
        }
    }

    public List<List<String>> findAllOptions(int handIdx) {
        tray = new ArrayList<Character>(game.getHands(handIdx));
        Collections.copy(game.getHands(handIdx), tray);
        ogTray = trayString(tray);

        initialTraySize = tray.size();
        String[] directions = {"across", "down"};
        for (var direction : directions) {
            swapDir();
            List<List<Integer>> anchors = findAnchors();
            crossCheckRes = crossCheck();
            for (var anchorPos : anchors) {
                if (board.isFilled(before(anchorPos))) {
                    List<Integer> scanPos = before(anchorPos);
                    String partialWord = "" + board.getTile(scanPos);
                    while (board.isFilled(before(scanPos))) {
                        scanPos = before(scanPos);
                        partialWord = board.getTile(scanPos) + partialWord;
                    }
                    DawgNode partialWordNode = dawg.getNode(partialWord);
                    if (partialWordNode != null) {
                        extendAfter(partialWord, partialWordNode, anchorPos, false);
                    }
                }
                else {
                    int limit = 0;
                    List<Integer> scanPos = anchorPos;
                    while (board.isEmpty(before(scanPos)) && !anchors.contains(before(scanPos))) {
                        limit++;
                        scanPos = before(scanPos);
                    }
                    beforePart("", dawg.getRoot(), anchorPos, limit);
                }
            }
        }
        return wordList;
    }

    public List<String> findBestOption(int handIdx) {
        tray = new ArrayList<Character>(game.getHands(handIdx));
        Collections.copy(game.getHands(handIdx), tray);
        ogTray = trayString(tray);

        initialTraySize = tray.size();
        String[] directions = {"across", "down"};
        for (var direction : directions) {
            swapDir();
            List<List<Integer>> anchors = findAnchors();
            crossCheckRes = crossCheck();
            for (var anchorPos : anchors) {
                if (board.isFilled(before(anchorPos))) {
                    List<Integer> scanPos = before(anchorPos);
                    String partialWord = "" + board.getTile(scanPos);
                    while (board.isFilled(before(scanPos))) {
                        scanPos = before(scanPos);
                        partialWord = board.getTile(scanPos) + partialWord;
                    }
                    DawgNode partialWordNode = dawg.getNode(partialWord);
                    if (partialWordNode != null) {
                        extendAfter(partialWord, partialWordNode, anchorPos, false);
                    }
                }
                else {
                    int limit = 0;
                    List<Integer> scanPos = anchorPos;
                    while (board.isEmpty(before(scanPos)) && !anchors.contains(before(scanPos))) {
                        limit++;
                        scanPos = before(scanPos);
                    }
                    beforePart("", dawg.getRoot(), anchorPos, limit);
                }
            }
        }
        return List.of(highestBoard.toString().toUpperCase(), highestWord.toUpperCase(), "" + highestScore, highestDir, highestPos);
    }

    public String trayString(List<Character> tray) {
        String retStr = "";
        for (var letter : tray) {
            retStr = letter + retStr;
        }
        return new StringBuilder(retStr).reverse().toString();
    }

    public Board getBoard() {
        return highestBoard;
    }
}