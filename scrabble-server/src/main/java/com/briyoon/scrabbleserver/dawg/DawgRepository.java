// package com.briyoon.scrabbleweb.dawg;

// import org.bson.Document;
// import org.springframework.data.mongodb.repository.MongoRepository;

// import com.briyoon.scrabbleweb.game.Game;

// public interface DawgRepository extends MongoRepository<Dawg, String> {

//     public default String insertDawg(Dawg dawg) {
//         Document dawgDoc = new Document(
//             {
//                 "_id": game.getGameID(),
//                 "dawg": dawg
//             }
//         );

//         String res = self.insert(gameDoc);

//         return res;
//     }
// }
