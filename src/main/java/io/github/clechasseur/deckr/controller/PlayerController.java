package io.github.clechasseur.deckr.controller;

import io.github.clechasseur.deckr.exception.PlayerWithoutGameException;
import io.github.clechasseur.deckr.model.CardAndSuit;
import io.github.clechasseur.deckr.model.Player;
import io.github.clechasseur.deckr.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/player")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Player createPlayer(@RequestBody Player player) {
        if (player.getGame() == null || player.getGame().getId() == null) {
            throw new PlayerWithoutGameException();
        }
        return playerService.createPlayer(player.getGame().getId(), player.getName());
    }

    @GetMapping("/{id}")
    public Player getPlayer(@PathVariable Long id) {
        return playerService.getPlayer(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
    }

    @GetMapping("/{playerId}/hand")
    public List<CardAndSuit> getCards(@PathVariable Long playerId) {
        return playerService.getCards(playerId);
    }

    @PutMapping("/{playerId}/hand")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deal(@PathVariable Long playerId, @RequestParam int numCards) {
        playerService.dealCards(playerId, numCards);
    }
}
