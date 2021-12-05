package io.github.clechasseur.deckr.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CardUtils {
    private CardUtils() {
    }

    public static List<String> cardsAsList(String cards) {
        String nonNullCards = StringUtils.orEmptyString(cards);
        if (nonNullCards.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(nonNullCards.split(",")));
    }
}
