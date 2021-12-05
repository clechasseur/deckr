package io.github.clechasseur.deckr.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class ArrayUtils {
    private ArrayUtils() {
    }

    public static <T> void shuffleArray(T[] elements) {
        // https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
        Random random = ThreadLocalRandom.current();
        for (int i = elements.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            T e = elements[index];
            elements[index] = elements[i];
            elements[i] = e;
        }
    }
}
