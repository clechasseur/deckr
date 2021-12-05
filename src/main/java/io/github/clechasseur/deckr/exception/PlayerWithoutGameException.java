package io.github.clechasseur.deckr.exception;

public class PlayerWithoutGameException extends RuntimeException {
    public PlayerWithoutGameException() {
        super("Player needs a game id");
    }
}
