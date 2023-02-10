package com.briyoon.scrabbleserver.solver;

import java.util.Comparator;


public class SortByScore implements Comparator<ValidMove> {
    public int compare(ValidMove a, ValidMove b)
    {
        return ((Integer) b.getScore()).compareTo(a.getScore());
    }
}
