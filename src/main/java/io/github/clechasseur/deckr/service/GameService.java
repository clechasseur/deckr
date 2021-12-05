package io.github.clechasseur.deckr.service;

import io.github.clechasseur.deckr.exception.GameNotFoundException;
import io.github.clechasseur.deckr.model.CardAndSuit;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.Player;
import io.github.clechasseur.deckr.model.PlayerAndValue;
import io.github.clechasseur.deckr.repository.GameRepository;
import io.github.clechasseur.deckr.util.CardUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createGame(String name) {
        Game game = new Game();
        game.setName(name);
        return gameRepository.save(game);
    }

    public Game getGame(Long id) {
        return gameRepository.findById(id).orElseThrow(() -> new GameNotFoundException(id));
    }

    public Game updateGame(Game game) {
        return gameRepository.save(game);
    }

    public void deleteGame(Long id) {
        try {
            gameRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new GameNotFoundException(id);
        }
    }

    public List<PlayerAndValue> getPlayersAndValues(Long gameId) {
        Game game = getGame(gameId);
        return game.getPlayers().stream()
                .map(player -> new PlayerAndValue(player, getPlayerValue(player)))
                .sorted(Comparator.comparingInt(PlayerAndValue::getValue).reversed()
                        .thenComparing(pv -> pv.getPlayer().getName()))
                .collect(Collectors.toList());
    }

    private static int getPlayerValue(Player player) {
        return CardUtils.cardsAsList(player.getHand()).stream()
                .map(CardAndSuit::parse)
                .mapToInt(cs -> cs.getCard().getValue())
                .sum();
    }
}
