package com.briyoon.scrabbleserver.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.briyoon.scrabbleserver.documents.GameDoc;
import com.briyoon.scrabbleserver.game.GameUtils.GameResponses;
import com.briyoon.scrabbleserver.solver.Solver;

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
        Solver solver = new Solver(game);

        // Verify and score move, update game
        List<List<String>> allPossibleMoves = solver.findAllOptions(1);
        List<String> validMove = null;
        for (var move : allPossibleMoves) {
            if (move.get(0).equals(newBoard.replace("\"", "").toUpperCase())) {
                validMove = move;
                break;
            }
        }

        if (validMove == null) {
            game.appendMsgArray("Invalid move submitted!");
            gameRepo.save(new GameDoc(game));
            return new Pair<Game, GameResponses>(game, GameResponses.INVALID_MOVE);
        }

        // Update game with player move
        game.setBoard(validMove.get(0).replace("\n", ""));

        // Update player hand and draw to fill hand
        List<Character> playerHand = game.getHands(1);
        for (var letter : validMove.get(1).split("")) {
            playerHand.remove((Character) letter.charAt(0));
        }

        game.drawTiles(playerHand, 7 - playerHand.size());

        // Find CPU move @TODO: return all moves a cpu can make, sort by score, then choose one depending on difficulty
        List<String> bestCPUMove = solver.findBestOption(0);

        // Update game with cpu move
        game.setBoard(bestCPUMove.get(0));

        // Update CPU hand and draw to fill hand
        List<Character> cpuHand = game.getHands(0);
        for (var letter : bestCPUMove.get(1).split("")) {
            cpuHand.remove((Character) letter.charAt(0));
        }

        game.drawTiles(cpuHand, 7 - cpuHand.size());

        game.incrementScores(
            List.of(
                Integer.parseInt(bestCPUMove.get(2)),
                Integer.parseInt(validMove.get(2))
            )
        );

        // Return updated game obj
        String[] statusStrs = {
            String.format(
                "You played %s for %s points!",
                validMove.get(1),
                validMove.get(2)
            ),
            String.format(
                "CPU played %s for %s points!",
                bestCPUMove.get(1),
                bestCPUMove.get(2)
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
            Scanner inScanner = new Scanner(new File("src/main/resources/wordlists/sowpods.txt"));
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
