package io.github.clechasseur.deckr.service;

import io.github.clechasseur.deckr.exception.GameNotFoundException;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.repository.GameRepository;
import org.springframework.stereotype.Service;

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

    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }
}
