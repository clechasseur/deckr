package io.github.clechasseur.deckr.model;

public enum Suit {
    Hearths("H"),
    Spades("S"),
    Clubs("C"),
    Diamonds("D");

    private final String symbol;

    Suit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Suit fromSymbol(String symbol) {
        for (Suit s : values()) {
            if (s.getSymbol().equals(symbol)) {
                return s;
            }
        }
        return null;
    }
}