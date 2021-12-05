package io.github.clechasseur.deckr.service;

import io.github.clechasseur.deckr.exception.GameWithoutShoeException;
import io.github.clechasseur.deckr.exception.PlayerNotFoundException;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.Player;
import io.github.clechasseur.deckr.model.Shoe;
import io.github.clechasseur.deckr.repository.PlayerRepository;
import io.github.clechasseur.deckr.repository.ShoeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {
    @InjectMocks
    private PlayerService playerService;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameService gameService;

    @Mock
    private ShoeRepository shoeRepository;

    @Test
    public void createPlayerReturnsNewPlayer() {
        Game game = Mockito.mock(Game.class);
        Mockito.when(gameService.getGame(1L)).thenReturn(game);
        Mockito.when(playerRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Player player = playerService.createPlayer(1L, "Player 1");

        Assertions.assertThat(player).isNotNull();
        Assertions.assertThat(player.getGame()).isEqualTo(game);
        Assertions.assertThat(player.getName()).isEqualTo("Player 1");
        Mockito.verify(gameService).getGame(1L);
        Mockito.verify(playerRepository).save(Mockito.any(Player.class));
    }

    @Test
    public void getPlayerWithNoPlayerThrowsException() {
        Mockito.when(playerRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> playerService.getPlayer(1L))
                .isInstanceOf(PlayerNotFoundException.class);
        Mockito.verify(playerRepository).findById(1L);
    }

    @Test
    public void getPlayerWithAPlayerReturnsPlayer() {
        Player player = Mockito.mock(Player.class);
        Mockito.when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Player actualPlayer = playerService.getPlayer(1L);

        Assertions.assertThat(actualPlayer).isNotNull();
        Assertions.assertThat(actualPlayer).isEqualTo(player);
        Mockito.verify(playerRepository).findById(1L);
    }

    @Test
    public void deletePlayerDeletesPlayer() {
        playerService.deletePlayer(1L);

        Mockito.verify(playerRepository).deleteById(1L);
    }

    @Test
    public void dealingCardsInAGameWithNoShoeThrowsException() {
        Player player = new Player();
        Game game = new Game();
        player.setGame(game);
        Mockito.when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Assertions.assertThatThrownBy(() -> playerService.dealCards(1L, 1))
                .isInstanceOf(GameWithoutShoeException.class);
        Mockito.verify(playerRepository).findById(1L);
    }

    @Test
    public void dealingCardsFromABigEnoughShoeDealsCardsAsExpected() {
        Player player = new Player();
        Game game = new Game();
        player.setGame(game);
        Shoe shoe = new Shoe();
        shoe.setCards("H4,D10,S3,C13,D1,H7");
        game.setShoe(shoe);
        Mockito.when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        playerService.dealCards(1L, 4);

        Mockito.verify(playerRepository).findById(1L);
        ArgumentCaptor<Shoe> shoeArgumentCaptor = ArgumentCaptor.forClass(Shoe.class);
        ArgumentCaptor<Player> playerArgumentCaptor = ArgumentCaptor.forClass(Player.class);
        Mockito.verify(shoeRepository).save(shoeArgumentCaptor.capture());
        Mockito.verify(playerRepository).save(playerArgumentCaptor.capture());
        Shoe actualShoe = shoeArgumentCaptor.getValue();
        Player actualPlayer = playerArgumentCaptor.getValue();
        Assertions.assertThat(actualShoe).isNotNull();
        Assertions.assertThat(actualShoe.getCards()).isEqualTo("D1,H7");
        Assertions.assertThat(actualPlayer).isNotNull();
        Assertions.assertThat(actualPlayer.getHand()).isEqualTo("H4,D10,S3,C13");
    }

    @Test
    public void dealingAllCardsLeftInShoeDealsCardsAsExpected() {
        Player player = new Player();
        Game game = new Game();
        player.setGame(game);
        Shoe shoe = new Shoe();
        shoe.setCards("H4,D10,S3,C13,D1,H7");
        game.setShoe(shoe);
        Mockito.when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        playerService.dealCards(1L, 6);

        Mockito.verify(playerRepository).findById(1L);
        ArgumentCaptor<Shoe> shoeArgumentCaptor = ArgumentCaptor.forClass(Shoe.class);
        ArgumentCaptor<Player> playerArgumentCaptor = ArgumentCaptor.forClass(Player.class);
        Mockito.verify(shoeRepository).save(shoeArgumentCaptor.capture());
        Mockito.verify(playerRepository).save(playerArgumentCaptor.capture());
        Shoe actualShoe = shoeArgumentCaptor.getValue();
        Player actualPlayer = playerArgumentCaptor.getValue();
        Assertions.assertThat(actualShoe).isNotNull();
        Assertions.assertThat(actualShoe.getCards()).isEmpty();
        Assertions.assertThat(actualPlayer).isNotNull();
        Assertions.assertThat(actualPlayer.getHand()).isEqualTo("H4,D10,S3,C13,D1,H7");
    }

    @Test
    public void dealingMoreCardsThanWhatIsLeftInShoeDealsRemainingCards() {
        Player player = new Player();
        Game game = new Game();
        player.setGame(game);
        Shoe shoe = new Shoe();
        shoe.setCards("H4,D10,S3,C13,D1,H7");
        game.setShoe(shoe);
        Mockito.when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        playerService.dealCards(1L, 8);

        Mockito.verify(playerRepository).findById(1L);
        ArgumentCaptor<Shoe> shoeArgumentCaptor = ArgumentCaptor.forClass(Shoe.class);
        ArgumentCaptor<Player> playerArgumentCaptor = ArgumentCaptor.forClass(Player.class);
        Mockito.verify(shoeRepository).save(shoeArgumentCaptor.capture());
        Mockito.verify(playerRepository).save(playerArgumentCaptor.capture());
        Shoe actualShoe = shoeArgumentCaptor.getValue();
        Player actualPlayer = playerArgumentCaptor.getValue();
        Assertions.assertThat(actualShoe).isNotNull();
        Assertions.assertThat(actualShoe.getCards()).isEmpty();
        Assertions.assertThat(actualPlayer).isNotNull();
        Assertions.assertThat(actualPlayer.getHand()).isEqualTo("H4,D10,S3,C13,D1,H7");
    }

    @Test
    public void dealingCardsFromAnEmptyShoeDoesNothing() {
        Player player = new Player();
        Game game = new Game();
        player.setGame(game);
        Shoe shoe = new Shoe();
        game.setShoe(shoe);
        Mockito.when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        playerService.dealCards(1L, 4);

        Mockito.verify(playerRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(playerRepository, shoeRepository);
    }
}
