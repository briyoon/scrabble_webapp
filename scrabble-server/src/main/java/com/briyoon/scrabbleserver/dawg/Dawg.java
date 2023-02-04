package com.briyoon.scrabbleserver.dawg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Stack;

import org.javatuples.Triplet;

public class Dawg implements Serializable {
    String previousWord = "";
    DawgNode root = new DawgNode();

    // Here is a list of nodes that have not been checked for duplication.
    private Stack<Triplet<DawgNode, Character, DawgNode>> uncheckedNodes = new Stack<>();

    // Here is a list of unique nodes that have been checked for duplication.
    private HashMap<DawgNode, DawgNode> minimizedNodes = new HashMap<>();

    public void insert(String word) throws Exception {
        if (word.compareTo(previousWord) <= 0) {
            throw new Exception("Error: Words must be inserted in alphabetical order");
        }

        // find common prefix between word and previous word
        int commonPrefix = 0;

        for (int i = 0; i < Math.min(word.length(), previousWord.length()); i++) {
            if (word.charAt(i) != previousWord.charAt(i)) {
                break;
            }
            commonPrefix++;
        }

        // Check the uncheckedNodes for redundant nodes, proceeding from last one down to the common prefix size. Then truncate the list at that point.
        minimize(commonPrefix);

        // add the suffix, starting from the correct node mid-way through the graph
        DawgNode node = uncheckedNodes.size() == 0 ? root : uncheckedNodes.peek().getValue2();

        for(Character letter : word.substring(commonPrefix).toCharArray()) {
            DawgNode nextNode = new DawgNode();
            node.children.put(letter, nextNode);
            uncheckedNodes.push(new Triplet<DawgNode, Character, DawgNode>(node, letter, nextNode));
            node = nextNode;
        }

        node.isWord = true;
        previousWord = word;
    }

    public void finish() {
        minimize(0);
        minimizedNodes.clear();
    }

    public void minimize(int downTo) {
        // proceed from the leaf up to a certain point
        // System.out.println(uncheckedNodes.size());
        for (int i = uncheckedNodes.size() - 1; i > downTo - 1; i--) {
            Triplet<DawgNode, Character, DawgNode> uncheckedNode = uncheckedNodes.pop();
            DawgNode parent = uncheckedNode.getValue0();
            Character letter = uncheckedNode.getValue1();
            DawgNode child = uncheckedNode.getValue2();

            if (minimizedNodes.containsKey(child)) {
                // replace the child with the previously encountered one
                parent.children.put(letter, minimizedNodes.get(child));
            }
            else {
                // add the state to the minimized nodes.
                minimizedNodes.put(child, child);
            }
        }
    }

    public Boolean find(String word) {
        DawgNode node = root;
        for (Character letter : word.toCharArray()) {
            if (!node.children.containsKey(letter)) {
                return false;
            }
            node = node.children.get(letter);
        }

        return node.isWord;
    }

    public DawgNode getRoot() {
        return root;
    }

    public DawgNode getNode(String word) {
        DawgNode current = root;
        for (char letter : word.toLowerCase().toCharArray()) {
            DawgNode node = current.children.get(letter);
            if (node == null) {return null;}
            current = node;
        }
        return current;
    }

    // public int nodeCount() {
    //     return minimizedNodes.size();
    // }

    // public int edgeCount() {
    //     int count = 0;
    //     for (DawgNode node : minimizedNodes.values()) {
    //         count += node.children.size();
    //     }

    //     return count;
    // }
}