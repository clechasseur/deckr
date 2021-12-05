package io.github.clechasseur.deckr.model;

import java.util.Objects;

public final class CardAndSuit {
    private final Card card;
    private final Suit suit;

    public CardAndSuit(Card card, Suit suit) {
        this.card = Objects.requireNonNull(card);
        this.suit = Objects.requireNonNull(suit);
    }

    public static CardAndSuit parse(String from) {
        String stringRepr = Objects.requireNonNull(from);
        Suit suit = Suit.fromSymbol(stringRepr.substring(0, 1));
        Card card = Card.fromValue(Integer.parseInt(stringRepr.substring(1)));
        return new CardAndSuit(card, suit);
    }

    public Card getCard() {
        return card;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardAndSuit that = (CardAndSuit) o;
        return card == that.card && suit == that.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(card, suit);
    }

    @Override
    public String toString() {
        return suit.getSymbol() + card.getValue();
    }
}
