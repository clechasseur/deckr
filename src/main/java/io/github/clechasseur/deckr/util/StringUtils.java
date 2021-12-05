package io.github.clechasseur.deckr.util;

public final class StringUtils {
    private StringUtils() {
    }

    public static String orEmptyString(String thisOr) {
        return thisOr != null ? thisOr : "";
    }
}
