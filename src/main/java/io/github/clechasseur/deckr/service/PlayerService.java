package io.github.clechasseur.deckr.service;

import io.github.clechasseur.deckr.exception.GameWithoutShoeException;
import io.github.clechasseur.deckr.exception.PlayerNotFoundException;
import io.github.clechasseur.deckr.model.CardAndSuit;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.Player;
import io.github.clechasseur.deckr.model.Shoe;
import io.github.clechasseur.deckr.repository.PlayerRepository;
import io.github.clechasseur.deckr.util.CardUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final GameService gameService;
    private final ShoeService shoeService;

    public PlayerService(PlayerRepository playerRepository, GameService gameService, ShoeService shoeService) {
        this.playerRepository = playerRepository;
        this.gameService = gameService;
        this.shoeService = shoeService;
    }

    @Transactional
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

    @Transactional
    public void deletePlayer(Long id) {
        Player player = getPlayer(id);
        Game game = player.getGame();
        List<Player> gamePlayers = game.getPlayers();
        Player playerToRemove = gamePlayers.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException(id));
        gamePlayers.remove(playerToRemove);
        gameService.updateGame(game);
    }

    public List<CardAndSuit> getCards(Long playerId) {
        Player player = getPlayer(playerId);
        return CardUtils.cardsAsList(player.getHand()).stream()
                .map(CardAndSuit::parse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void dealCards(Long playerId, int numCards) {
        Player player = getPlayer(playerId);
        Shoe shoe = player.getGame().getShoe();
        if (shoe == null) {
            throw new GameWithoutShoeException(player.getGame().getId());
        }
        List<String> shoeCards = CardUtils.cardsAsList(shoe.getCards());
        if (!shoeCards.isEmpty()) {
            List<String> playerHand = CardUtils.cardsAsList(player.getHand());
            playerHand.addAll(shoeCards.stream().limit(numCards).collect(Collectors.toList()));
            player.setHand(String.join(",", playerHand));
            shoe.setCards(shoeCards.stream().skip(numCards).collect(Collectors.joining(",")));
            shoeService.updateShoe(shoe);
            playerRepository.save(player);
        }
    }
}
