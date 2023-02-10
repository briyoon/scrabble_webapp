package com.briyoon.scrabbleserver.dawg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class DawgNode implements Serializable {
    public static int nextId = 0;

    public int id;
    public Boolean isWord = false;
    public HashMap<Character, DawgNode> children = new HashMap<>();

    public DawgNode() {
        this.id = nextId;
        nextId++;
    }

    @Override
    public String toString() {
        List<String> arr = new ArrayList<>();

        arr.add(this.isWord ? "1" : "0");

        for (Entry<Character, DawgNode> set : children.entrySet()) {
            arr.add(String.valueOf(set.getKey()));
            arr.add(String.valueOf(set.getValue().id));
        }

        return String.join("", arr);
    }

    public DawgNode getChild(Character letter) {
        return this.children.get(letter);
    }

    public HashMap<Character, DawgNode> getChildren() {
        return this.children;
    }

    public Boolean isWord() {
        return this.isWord;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.toString().equals(obj.toString());
    }
}
