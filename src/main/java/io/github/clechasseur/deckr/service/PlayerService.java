package io.github.clechasseur.deckr.service;

import io.github.clechasseur.deckr.exception.GameWithoutShoeException;
import io.github.clechasseur.deckr.exception.PlayerNotFoundException;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.Player;
import io.github.clechasseur.deckr.model.Shoe;
import io.github.clechasseur.deckr.repository.GameRepository;
import io.github.clechasseur.deckr.repository.PlayerRepository;
import io.github.clechasseur.deckr.repository.ShoeRepository;
import io.github.clechasseur.deckr.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final GameService gameService;
    private final ShoeRepository shoeRepository;

    public PlayerService(PlayerRepository playerRepository, GameService gameService, ShoeRepository shoeRepository) {
        this.playerRepository = playerRepository;
        this.gameService = gameService;
        this.shoeRepository = shoeRepository;
    }

    public Player createPlayer(Long gameId, String name) {
        Game game = gameService.getGame(gameId);
        Player player = new Player();
        player.setGame(game);
        player.setName(name);
        return playerRepository.save(player);
    }

    public Player getPlayer(Long id) {
        return playerRepository.findById(id).orElseThrow(() -> new PlayerNotFoundException(id));
    }

    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }

    public void dealCardsToPlayer(Long id, int numCards) {
        Player player = getPlayer(id);
        Shoe shoe = player.getGame().getShoe();
        if (shoe == null) {
            throw new GameWithoutShoeException(player.getGame().getId());
        }
        List<String> shoeCards = cardsAsList(shoe.getCards());
        if (!shoeCards.isEmpty()) {
            List<String> playerHand = cardsAsList(player.getHand());
            playerHand.addAll(shoeCards.stream().limit(numCards).collect(Collectors.toList()));
            player.setHand(String.join(",", playerHand));
            shoe.setCards(shoeCards.stream().skip(numCards).collect(Collectors.joining(",")));
            shoeRepository.save(shoe);
            playerRepository.save(player);
        }
    }

    private static List<String> cardsAsList(String cards) {
        String nonEmptyCards = StringUtils.orEmptyString(cards);
        if (nonEmptyCards.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(nonEmptyCards.split(",")));
    }
}
