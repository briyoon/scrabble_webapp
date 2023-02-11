package com.briyoon.scrabbleserver.game;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.briyoon.scrabbleserver.documents.GameDoc;
import com.briyoon.scrabbleserver.game.GameUtils.GameResponses;
import com.briyoon.scrabbleserver.solver.Solver2;
import com.briyoon.scrabbleserver.solver.SortByScore;
import com.briyoon.scrabbleserver.solver.ValidMove;

@Service
public class GameService {

    @Autowired
    GameRepository gameRepo;

    public GameDoc getGameDoc(String gameID) {
        // Find game by ID in DB and return
        GameDoc gameDoc = gameRepo.findGameByID(gameID);

        return gameDoc;
    }

    public Game getGame(String gameID) {
        // Find game by ID in DB and return
        GameDoc gameDoc = gameRepo.findGameByID(gameID);

        // Create game doc
        Game game = new Game(gameDoc);

        return game;
    }

    public Game createGame() {
        // Generate random id
        String gameID = generateGameID();

        // Create game with random ID
        Game game = new Game(gameID);

        // Insert into DB
        gameRepo.save(new GameDoc(game));

        // Return game if no errors
        return game;
    }

    public Pair<Game, GameResponses> updateGame(String gameID, String newBoard) {
        // Find game in DB
        GameDoc gameDoc = gameRepo.findGameByID(gameID);

        if (gameDoc == null) {
            return new Pair<Game, GameResponses>(null, GameResponses.GAME_NOT_FOUND);
        }

        Game game = new Game(gameDoc);

        // Find list of possible moves
        Solver2 solver = new Solver2();
        HashMap<String, ValidMove> allPossibleMoves = solver.findAllMoves(game.getBoard(), game.getDawg(), game.getHands(1));

        // Verify and score move, update game
        ValidMove validMove;
        if (allPossibleMoves.containsKey(newBoard)) {
            validMove = allPossibleMoves.get(newBoard);
        }
        else {
            game.appendMsgArray("Invalid move submitted!");
            gameRepo.save(new GameDoc(game));
            return new Pair<Game, GameResponses>(game, GameResponses.INVALID_MOVE);
        }

        // Update game with player move
        game.setBoard(newBoard);

        // Update player hand and draw to fill hand
        List<Character> playerHand = game.getHands(1);
        for (Character letter : validMove.getWord().toLowerCase().toCharArray()) {
            playerHand.remove(letter);
        }

        game.drawTiles(playerHand, 7 - playerHand.size());

        // Find CPU move @TODO: return all moves a cpu can make, sort by score, then choose one depending on difficulty
        HashMap<String, ValidMove> allCPUMoves = solver.findAllMoves(game.getBoard(), game.getDawg(), game.getHands(0));
        List<Map.Entry<String,ValidMove>> sortedMovesByScore = allCPUMoves
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(new SortByScore()))
            .collect(Collectors.toList());

        String cpuBoard = sortedMovesByScore.get(0).getKey();
        ValidMove cpuMove = sortedMovesByScore.get(0).getValue();

        // Update game with cpu move
        game.setBoard(cpuBoard);

        // Update CPU hand and draw to fill hand
        List<Character> cpuHand = game.getHands(0);
        for (Character letter : cpuMove.getWord().toLowerCase().toCharArray()) {
            cpuHand.remove(letter);
        }

        game.drawTiles(cpuHand, 7 - cpuHand.size());

        game.incrementScores(
            List.of(
                cpuMove.getScore(),
                validMove.getScore()
            )
        );

        // Return updated game obj
        String[] statusStrs = {
            String.format(
                "You played %s for %s points!",
                validMove.getWord(),
                validMove.getScore()
            ),
            String.format(
                "CPU played %s for %s points!",
                cpuMove.getWord(),
                cpuMove.getScore()
            )
        };

        game.appendMsgArray(statusStrs);

        // Overwrite game in DB
        gameRepo.save(new GameDoc(game));

        return new Pair<Game, GameResponses>(game, GameResponses.OK);
    }

    private String generateGameID() {
        String gameID = "";
        List<String> input = new ArrayList<>();

        // Read dictionary
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("wordlists/sowpods.txt");
            Scanner inScanner = new Scanner(is);
            while (inScanner.hasNextLine()) {
                input.add(inScanner.nextLine());
            }
            inScanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Choose 3 random words to construct a verified unique gameID
        for (int errors = 0; gameID == "" && errors < 10; errors++) {
            Random rand = new Random();

            List<String> randStrs = new ArrayList<>(3);
            for (int i = 0; i < 3; i++) {
                randStrs.add(input.get(rand.nextInt(input.size())));
            }

            gameID = String.format("%s-%s-%s", randStrs.get(0), randStrs.get(1), randStrs.get(2));

            if (gameRepo.findGameByID(gameID) != null) {
                gameID = "";
            }
        }

        // Return verified unique gameID
        return gameID;
    }
}
