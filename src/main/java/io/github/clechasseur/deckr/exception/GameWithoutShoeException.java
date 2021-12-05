package io.github.clechasseur.deckr.exception;

public class GameWithoutShoeException extends RuntimeException {
    public GameWithoutShoeException(Long id) {
        super("Game with id " + id + " has no active shoe");
    }
}
