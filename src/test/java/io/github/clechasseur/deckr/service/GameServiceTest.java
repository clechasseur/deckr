package io.github.clechasseur.deckr.service;

import io.github.clechasseur.deckr.exception.GameNotFoundException;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.Player;
import io.github.clechasseur.deckr.model.PlayerAndValue;
import io.github.clechasseur.deckr.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {
    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @Test
    public void createGameReturnsNewGame() {
        when(gameRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Game game = gameService.createGame("Test game");

        assertThat(game).isNotNull();
        assertThat(game.getName()).isEqualTo("Test game");
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    public void getGameWithNoGameThrowsException() {
        when(gameRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.getGame(1L)).isInstanceOf(GameNotFoundException.class);
        verify(gameRepository).findById(1L);
    }

    @Test
    public void getGameWithAGameReturnsGame() {
        Game game = mock(Game.class);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        Game actualGame = gameService.getGame(1L);

        assertThat(actualGame).isNotNull();
        assertThat(actualGame).isEqualTo(game);
        verify(gameRepository).findById(1L);
    }

    @Test
    public void updateGameReturnsUpdatedGame() {
        when(gameRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Game game = mock(Game.class);
        Game updatedGame = gameService.updateGame(game);

        assertThat(updatedGame).isNotNull();
        assertThat(updatedGame).isEqualTo(game);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    public void deleteGameDeletesGame() {
        gameService.deleteGame(1L);

        verify(gameRepository).deleteById(1L);
    }

    @Test
    public void deleteGameOnANonExistentGameThrowsException() {
        doThrow(new EmptyResultDataAccessException(1)).when(gameRepository).deleteById(any());

        assertThatThrownBy(() -> gameService.deleteGame(1L)).isInstanceOf(GameNotFoundException.class);
    }

    @Test
    public void getPlayersAndValuesReturnsAllPlayersAndValuesProperlySorted() {
        Game game = new Game();
        game.setPlayers(new ArrayList<>());
        Player player1 = createPlayerIn(game, "Player 1", "S10,C3,H1");
        Player player2 = createPlayerIn(game, "Player 2", "D2,H12,H7,H9");
        Player player3 = createPlayerIn(game, "Player 3", "S1,D6,C13,H1");
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        List<PlayerAndValue> players = gameService.getPlayersAndValues(1L);

        assertThat(players).isNotNull();
        assertThat(players).isEqualTo(Arrays.asList(
                new PlayerAndValue(player2, 30),
                new PlayerAndValue(player3, 21),
                new PlayerAndValue(player1, 14)
        ));
    }

    private static Player createPlayerIn(Game game, String name, String hand) {
        Player player = new Player();
        player.setGame(game);
        player.setName(name);
        player.setHand(hand);
        game.getPlayers().add(player);
        return player;
    }
}
