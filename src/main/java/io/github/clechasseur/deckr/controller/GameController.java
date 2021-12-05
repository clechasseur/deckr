package io.github.clechasseur.deckr.controller;

import io.github.clechasseur.deckr.exception.GameWithoutShoeException;
import io.github.clechasseur.deckr.model.CardAndSuit;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.PlayerAndValue;
import io.github.clechasseur.deckr.model.Shoe;
import io.github.clechasseur.deckr.model.Suit;
import io.github.clechasseur.deckr.service.GameService;
import io.github.clechasseur.deckr.service.ShoeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;
    private final ShoeService shoeService;

    public GameController(GameService gameService, ShoeService shoeService) {
        this.gameService = gameService;
        this.shoeService = shoeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Game createGame(@RequestBody Game game) {
        return gameService.createGame(game.getName());
    }

    @GetMapping("/{id}")
    public Game getGame(@PathVariable Long id) {
        return gameService.getGame(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
    }

    @PostMapping("/{gameId}/shoe")
    @ResponseStatus(HttpStatus.CREATED)
    public Shoe createShoe(@PathVariable Long gameId) {
        return shoeService.createShoe(gameId);
    }

    @GetMapping("/{gameId}/shoe")
    public Shoe getShoe(@PathVariable Long gameId) {
        return shoeService.getShoe(getGameShoe(gameId).getId());
    }

    @PutMapping("/{gameId}/shoe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addDeckToShoe(@PathVariable Long gameId) {
        shoeService.addDeckToShoe(getGameShoe(gameId).getId());
    }

    @PatchMapping("/{gameId}/shoe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void shuffleShoe(@PathVariable Long gameId) {
        shoeService.shuffle(getGameShoe(gameId).getId());
    }

    @GetMapping("/{gameId}/shoe/suits")
    public Map<Suit, Integer> getCountOfCardsLeftInShoeBySuit(@PathVariable Long gameId) {
        return shoeService.getCountOfCardsLeftBySuit(getGameShoe(gameId).getId());
    }

    @GetMapping("/{gameId}/shoe/cards")
    public List<CardAndSuit> getCardsLeftInShoe(@PathVariable Long gameId) {
        return shoeService.getCardsLeft(getGameShoe(gameId).getId());
    }

    @GetMapping("/{gameId}/players")
    public List<PlayerAndValue> getPlayersAndValues(@PathVariable Long gameId) {
        return gameService.getPlayersAndValues(gameId);
    }

    private Shoe getGameShoe(Long gameId) {
        Game game = gameService.getGame(gameId);
        if (game.getShoe() == null) {
            throw new GameWithoutShoeException(gameId);
        }
        return game.getShoe();
    }
}
