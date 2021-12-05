package io.github.clechasseur.deckr.controller;

import io.github.clechasseur.deckr.exception.PlayerWithoutGameException;
import io.github.clechasseur.deckr.model.CardAndSuit;
import io.github.clechasseur.deckr.model.Player;
import io.github.clechasseur.deckr.service.PlayerService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/player")
public class PlayerController {
    private final PlayerService playerService;
    private final PlayerModelAssembler playerModelAssembler;

    public PlayerController(PlayerService playerService, PlayerModelAssembler playerModelAssembler) {
        this.playerService = playerService;
        this.playerModelAssembler = playerModelAssembler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Player> createPlayer(@RequestBody Player player) {
        if (player.getGame() == null || player.getGame().getId() == null) {
            throw new PlayerWithoutGameException();
        }
        return playerModelAssembler.toModel(playerService.createPlayer(player.getGame().getId(), player.getName()));
    }

    @GetMapping("/{id}")
    public EntityModel<Player> getPlayer(@PathVariable Long id) {
        return playerModelAssembler.toModel(playerService.getPlayer(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
    }

    @GetMapping("/{playerId}/hand")
    public CollectionModel<EntityModel<CardAndSuit>> getCards(@PathVariable Long playerId) {
        List<CardAndSuit> cards = playerService.getCards(playerId);
        return CollectionModel.of(cards.stream().map(EntityModel::of).collect(Collectors.toList()),
                linkTo(methodOn(PlayerController.class).getCards(playerId)).withSelfRel(),
                linkTo(methodOn(PlayerController.class).getPlayer(playerId)).withRel("player"));
    }

    @PutMapping("/{playerId}/hand")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deal(@PathVariable Long playerId, @RequestParam int numCards) {
        playerService.dealCards(playerId, numCards);
    }
}
