package com.briyoon.scrabbleserver.game;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.briyoon.scrabbleserver.documents.GameDoc;
import com.briyoon.scrabbleserver.game.GameUtils.GameResponses;

@RestController
@RequestMapping(path="api/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping
    public ResponseEntity<?> getGame(String gameID) {
        ResponseEntity<?> res;

        System.out.println("gameID: gameID");

        GameDoc gameDoc = this.gameService.getGameDoc(gameID);

        if (gameDoc != null) {
            JSONObject json = gameDoc.toJSON();

            res = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(json.toString());
        }
        else {
            res = ResponseEntity.notFound()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
        }
        return res;
    }

    @PostMapping
    public ResponseEntity<?> postGame() {
        ResponseEntity<?> res;

        Game game = this.gameService.createGame();

        if (game != null) {
            JSONObject json = game.toJSON();

            res = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(json.toString());
        }
        else {
            res = ResponseEntity.internalServerError()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body("Game could not be created");
        }
        return res;
    }

    @PatchMapping
    public ResponseEntity<?> patchGame(String gameID, String newBoard) {
        ResponseEntity<?> res;

        Pair<Game, GameResponses> gamePair = this.gameService.updateGame(gameID, newBoard);
        Game game = gamePair.getValue0();
        GameResponses statusEnum = gamePair.getValue1();

        JSONObject json = new JSONObject();

        switch (statusEnum) {
            case GAME_NOT_FOUND:
                json.put("status", "Game not found");

                res = ResponseEntity.internalServerError() // figure out the proper html res code
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(json.toString());
                break;
            case INVALID_MOVE:
                json = game.toJSON();

                res = ResponseEntity.unprocessableEntity()
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(json.toString());
                break;
            default:
                json = game.toJSON();

                res = ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(json.toString());
                break;
        }
        return res;
    }
}
