package com.briyoon.scrabbleserver.game;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.briyoon.scrabbleserver.documents.GameDoc;

public interface GameRepository extends MongoRepository<GameDoc, String> {
    @Query("{id:'?0'}")
    GameDoc findGameByID(String id);

    // @Query(value="{category:'?0'}", fields="{'id' : 1, 'quantity' : 1}")
    // List<Games> findAll(String category);

    public long count();
}

// import java.util.List;
// import org.bson.Document;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.stereotype.Service;

// import com.briyoon.scrabbleweb.board.Board;

// @Service
// public class GameRepository {
//     @Autowired
// 	private MongoTemplate mongoTemplate;

//     public Game findGameByID(String gameID) {
//         Document document = mongoTemplate.findById(
//             gameID,
//             Document.class,
//             "games"
//         );

//         if (document == null) {
//             return null;
//         }
//         else {
//             Document bTemp = (Document) document.get("board");
//             Board board = new Board((Integer) bTemp.get("size"), (List<Character>) bTemp.get("tiles"));
//             List<List<Character>> hands = (List<List<Character>>) document.get("hands");
//             List<Integer> scores = (List<Integer>) document.get("scores");
//             List<Character> drawPile = (List<Character>) document.get("drawPile");

//             return new Game(gameID, board, hands, scores, drawPile);
//         }
//     }

//     public Document insertGame(Game game) {
//         Document gameDoc = new Document()
//             .append("_id", game.getGameID())
//             .append("board", game.getBoard())
//             // .append("dawg_id", mongoTemplate.insert(new Document().append("dawg", game.getDawg()), "dawgs"))
//             .append("hands", game.getHands())
//             .append("scores", game.getScores())
//             .append("drawPile", game.getDrawPile());

//         Document res = mongoTemplate.insert(gameDoc, "games");

//         return res;
//     }

//     public Document updateGame(Game game) {
//         Document gameDoc = new Document()
//         .append("_id", game.getGameID())
//         .append("board", game.getBoard())
//         // .append("dawg_id", mongoTemplate.insert(new Document().append("dawg", game.getDawg()), "dawgs"))
//         .append("hands", game.getHands())
//         .append("scores", game.getScores())
//         .append("drawPile", game.getDrawPile());

//         Document res = mongoTemplate.insert(gameDoc, "games");

//         return res;
//     }
// }