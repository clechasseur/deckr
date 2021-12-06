package io.github.clechasseur.deckr.controller;

import io.github.clechasseur.deckr.exception.GameWithoutShoeException;
import io.github.clechasseur.deckr.model.CardAndSuit;
import io.github.clechasseur.deckr.model.CountsBySuit;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.PlayerAndValue;
import io.github.clechasseur.deckr.model.Shoe;
import io.github.clechasseur.deckr.service.GameService;
import io.github.clechasseur.deckr.service.ShoeService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;
    private final ShoeService shoeService;
    private final GameModelAssembler gameModelAssembler;
    private final ShoeModelAssembler shoeModelAssembler;

    public GameController(
            GameService gameService,
            ShoeService shoeService,
            GameModelAssembler gameModelAssembler,
            ShoeModelAssembler shoeModelAssembler
    ) {
        this.gameService = gameService;
        this.shoeService = shoeService;
        this.gameModelAssembler = gameModelAssembler;
        this.shoeModelAssembler = shoeModelAssembler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Game> createGame(@RequestBody Game game) {
        return gameModelAssembler.toModel(gameService.createGame(game.getName()));
    }

    @GetMapping("/{id}")
    public EntityModel<Game> getGame(@PathVariable Long id) {
        return gameModelAssembler.toModel(gameService.getGame(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
    }

    @PostMapping("/{gameId}/shoe")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Shoe> createShoe(@PathVariable Long gameId) {
        return shoeModelAssembler.toModel(shoeService.createShoe(gameId));
    }

    @GetMapping("/{gameId}/shoe")
    @Transactional
    public EntityModel<Shoe> getShoe(@PathVariable Long gameId) {
        return shoeModelAssembler.toModel(shoeService.getShoe(getGameShoe(gameId).getId()));
    }

    @PutMapping("/{gameId}/shoe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addDeckToShoe(@PathVariable Long gameId) {
        shoeService.addDeckToShoe(getGameShoe(gameId).getId());
    }

    @PatchMapping("/{gameId}/shoe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void shuffleShoe(@PathVariable Long gameId) {
        shoeService.shuffle(getGameShoe(gameId).getId());
    }

    @GetMapping("/{gameId}/shoe/suits")
    @Transactional
    public EntityModel<CountsBySuit> getCountOfCardsLeftInShoeBySuit(@PathVariable Long gameId) {
        CountsBySuit counts = new CountsBySuit(shoeService.getCountOfCardsLeftBySuit(getGameShoe(gameId).getId()));
        return EntityModel.of(counts,
                linkTo(methodOn(GameController.class).getCountOfCardsLeftInShoeBySuit(gameId)).withSelfRel(),
                linkTo(methodOn(GameController.class).getShoe(gameId)).withRel("shoe"),
                linkTo(methodOn(GameController.class).getGame(gameId)).withRel("game"));
    }

    @GetMapping("/{gameId}/shoe/cards")
    @Transactional
    public CollectionModel<EntityModel<CardAndSuit>> getCardsLeftInShoe(@PathVariable Long gameId) {
        List<CardAndSuit> cards = shoeService.getCardsLeft(getGameShoe(gameId).getId());
        return CollectionModel.of(cards.stream().map(EntityModel::of).collect(Collectors.toList()),
                linkTo(methodOn(GameController.class).getCardsLeftInShoe(gameId)).withSelfRel(),
                linkTo(methodOn(GameController.class).getShoe(gameId)).withRel("shoe"),
                linkTo(methodOn(GameController.class).getGame(gameId)).withRel("game"));
    }

    @GetMapping("/{gameId}/players")
    public CollectionModel<EntityModel<PlayerAndValue>> getPlayersAndValues(@PathVariable Long gameId) {
        List<PlayerAndValue> players = gameService.getPlayersAndValues(gameId);
        return CollectionModel.of(players.stream().map(EntityModel::of).collect(Collectors.toList()),
                linkTo(methodOn(GameController.class).getPlayersAndValues(gameId)).withSelfRel(),
                linkTo(methodOn(GameController.class).getGame(gameId)).withRel("game"));
    }

    private Shoe getGameShoe(Long gameId) {
        Game game = gameService.getGame(gameId);
        if (game.getShoe() == null) {
            throw new GameWithoutShoeException(gameId);
        }
        return game.getShoe();
    }
}
