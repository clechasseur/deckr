package io.github.clechasseur.deckr.exception;

public class ShoeNotFoundException extends RuntimeException {
    public ShoeNotFoundException(Long id) {
        super("Shoe with id " + id + " not found");
    }
}
