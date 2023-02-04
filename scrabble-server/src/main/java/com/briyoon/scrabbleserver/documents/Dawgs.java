package com.briyoon.scrabbleserver.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.briyoon.scrabbleserver.dawg.Dawg;

@Document("dawgs")
public class Dawgs {
    @Id
    private String id;

    private Dawg dawg;

    public Dawgs(String id, Dawg dawg) {
        super();
        this.id = id;
        this.dawg = dawg;
    }

    public String getId() {
        return id;
    }

    public Dawg getDawg() {
        return dawg;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDawg(Dawg dawg) {
        this.dawg = dawg;
    }
}
