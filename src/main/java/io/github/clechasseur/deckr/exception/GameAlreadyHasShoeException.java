package io.github.clechasseur.deckr.exception;

public class GameAlreadyHasShoeException extends RuntimeException {
    public GameAlreadyHasShoeException(Long id) {
        super("Game with id " + id + " already has a shoe");
    }
}
