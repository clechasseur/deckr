package io.github.clechasseur.deckr.model;

public enum Suit {
    Hearts("H"),
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

    @Override
    public String toString() {
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
