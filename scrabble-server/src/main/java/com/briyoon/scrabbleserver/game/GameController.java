package com.briyoon.scrabbleserver.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(path="api/games")
public class GameController {
    @Autowired
    private GameService gameService;

    @GetMapping
    public ResponseEntity<?> getGame(String gameID) {
        ResponseEntity<?> res;

        return res;
    }

    @PostMapping
    public ResponseEntity<?> postGame() {
        ResponseEntity<?> res;

        return res;
    }

    @PatchMapping
    public ResponseEntity<?> patchGame(String gameID, String newBoard) {
        ResponseEntity<?> res;

        return res;
    }

}
