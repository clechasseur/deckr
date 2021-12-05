package io.github.clechasseur.deckr.model;

public enum Card {
    Ace(1),
    Two(2),
    Three(3),
    Four(4),
    Five(5),
    Six(6),
    Seven(7),
    Eight(8),
    Nine(9),
    Ten(10),
    Jack(11),
    Queen(12),
    King(13);

    private final int value;

    Card(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static Card fromValue(int value) {
        for (Card c : values()) {
            if (c.getValue() == value) {
                return c;
            }
        }
        return null;
    }
}
