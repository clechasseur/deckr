package io.github.clechasseur.deckr.model;

import java.util.Map;

public class CountsBySuit {
    private Map<Suit, Integer> counts;

    public CountsBySuit() {
    }

    public CountsBySuit(Map<Suit, Integer> counts) {
        this.counts = counts;
    }

    public Map<Suit, Integer> getCounts() {
        return counts;
    }

    public void setCounts(Map<Suit, Integer> counts) {
        this.counts = counts;
    }
}
