package io.github.clechasseur.deckr.service;

import io.github.clechasseur.deckr.exception.GameWithoutShoeException;
import io.github.clechasseur.deckr.exception.PlayerNotFoundException;
import io.github.clechasseur.deckr.model.Card;
import io.github.clechasseur.deckr.model.CardAndSuit;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.Player;
import io.github.clechasseur.deckr.model.Shoe;
import io.github.clechasseur.deckr.model.Suit;
import io.github.clechasseur.deckr.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {
    @InjectMocks
    private PlayerService playerService;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameService gameService;

    @Mock
    private ShoeService shoeService;

    @Test
    public void createPlayerReturnsNewPlayer() {
        Game game = mock(Game.class);
        when(gameService.getGame(1L)).thenReturn(game);
        when(playerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Player player = playerService.createPlayer(1L, "Player 1");

        assertThat(player).isNotNull();
        assertThat(player.getGame()).isEqualTo(game);
        assertThat(player.getName()).isEqualTo("Player 1");
        verify(gameService).getGame(1L);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    public void getPlayerWithNoPlayerThrowsException() {
        when(playerRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.getPlayer(1L)).isInstanceOf(PlayerNotFoundException.class);
        verify(playerRepository).findById(1L);
    }

    @Test
    public void getPlayerWithAPlayerReturnsPlayer() {
        Player player = mock(Player.class);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Player actualPlayer = playerService.getPlayer(1L);

        assertThat(actualPlayer).isNotNull();
        assertThat(actualPlayer).isEqualTo(player);
        verify(playerRepository).findById(1L);
    }

    @Test
    public void deletePlayerDeletesPlayer() {
        Player player = new Player();
        player.setId(1L);
        Game game = new Game();
        player.setGame(game);
        game.setPlayers(new ArrayList<>(Collections.singletonList(player)));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        playerService.deletePlayer(1L);

        verify(playerRepository).findById(1L);
        ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameService).updateGame(gameArgumentCaptor.capture());
        Game actualGame = gameArgumentCaptor.getValue();
        assertThat(game).isNotNull();
        assertThat(game.getPlayers()).isNullOrEmpty();
    }

    @Test
    public void deletePlayerOnANonExistentPlayerThrowsException() {
        assertThatThrownBy(() -> playerService.deletePlayer(1L)).isInstanceOf(PlayerNotFoundException.class);
    }

    @Test
    public void getCardsReturnsProperCardsForPlayer() {
        Player player = new Player();
        player.setHand("H9,D1,S13,C5");
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        List<CardAndSuit> cards = playerService.getCards(1L);

        assertThat(cards).isNotNull();
        assertThat(cards).isEqualTo(Arrays.asList(
                new CardAndSuit(Card.Nine, Suit.Hearts),
                new CardAndSuit(Card.Ace, Suit.Diamonds),
                new CardAndSuit(Card.King, Suit.Spades),
                new CardAndSuit(Card.Five, Suit.Clubs)
        ));
        verify(playerRepository).findById(1L);
    }

    @Test
    public void dealingCardsInAGameWithNoShoeThrowsException() {
        Player player = new Player();
        Game game = new Game();
        player.setGame(game);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        assertThatThrownBy(() -> playerService.dealCards(1L, 1))
                .isInstanceOf(GameWithoutShoeException.class);
        verify(playerRepository).findById(1L);
    }

    @Test
    public void dealingCardsFromABigEnoughShoeDealsCardsAsExpected() {
        Player player = new Player();
        Game game = new Game();
        player.setGame(game);
        Shoe shoe = new Shoe();
        shoe.setCards("H4,D10,S3,C13,D1,H7");
        game.setShoe(shoe);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        playerService.dealCards(1L, 4);

        verify(playerRepository).findById(1L);
        ArgumentCaptor<Shoe> shoeArgumentCaptor = ArgumentCaptor.forClass(Shoe.class);
        ArgumentCaptor<Player> playerArgumentCaptor = ArgumentCaptor.forClass(Player.class);
        verify(shoeService).updateShoe(shoeArgumentCaptor.capture());
        verify(playerRepository).save(playerArgumentCaptor.capture());
        Shoe actualShoe = shoeArgumentCaptor.getValue();
        Player actualPlayer = playerArgumentCaptor.getValue();
        assertThat(actualShoe).isNotNull();
        assertThat(actualShoe.getCards()).isEqualTo("D1,H7");
        assertThat(actualPlayer).isNotNull();
        assertThat(actualPlayer.getHand()).isEqualTo("H4,D10,S3,C13");
    }

    @Test
    public void dealingAllCardsLeftInShoeDealsCardsAsExpected() {
        Player player = new Player();
        Game game = new Game();
        player.setGame(game);
        Shoe shoe = new Shoe();
        shoe.setCards("H4,D10,S3,C13,D1,H7");
        game.setShoe(shoe);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        playerService.dealCards(1L, 6);

        verify(playerRepository).findById(1L);
        ArgumentCaptor<Shoe> shoeArgumentCaptor = ArgumentCaptor.forClass(Shoe.class);
        ArgumentCaptor<Player> playerArgumentCaptor = ArgumentCaptor.forClass(Player.class);
        verify(shoeService).updateShoe(shoeArgumentCaptor.capture());
        verify(playerRepository).save(playerArgumentCaptor.capture());
        Shoe actualShoe = shoeArgumentCaptor.getValue();
        Player actualPlayer = playerArgumentCaptor.getValue();
        assertThat(actualShoe).isNotNull();
        assertThat(actualShoe.getCards()).isEmpty();
        assertThat(actualPlayer).isNotNull();
        assertThat(actualPlayer.getHand()).isEqualTo("H4,D10,S3,C13,D1,H7");
    }

    @Test
    public void dealingMoreCardsThanWhatIsLeftInShoeDealsRemainingCards() {
        Player player = new Player();
        Game game = new Game();
        player.setGame(game);
        Shoe shoe = new Shoe();
        shoe.setCards("H4,D10,S3,C13,D1,H7");
        game.setShoe(shoe);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        playerService.dealCards(1L, 8);

        verify(playerRepository).findById(1L);
        ArgumentCaptor<Shoe> shoeArgumentCaptor = ArgumentCaptor.forClass(Shoe.class);
        ArgumentCaptor<Player> playerArgumentCaptor = ArgumentCaptor.forClass(Player.class);
        verify(shoeService).updateShoe(shoeArgumentCaptor.capture());
        verify(playerRepository).save(playerArgumentCaptor.capture());
        Shoe actualShoe = shoeArgumentCaptor.getValue();
        Player actualPlayer = playerArgumentCaptor.getValue();
        assertThat(actualShoe).isNotNull();
        assertThat(actualShoe.getCards()).isEmpty();
        assertThat(actualPlayer).isNotNull();
        assertThat(actualPlayer.getHand()).isEqualTo("H4,D10,S3,C13,D1,H7");
    }

    @Test
    public void dealingCardsFromAnEmptyShoeDoesNothing() {
        Player player = new Player();
        Game game = new Game();
        player.setGame(game);
        Shoe shoe = new Shoe();
        game.setShoe(shoe);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        playerService.dealCards(1L, 4);

        verify(playerRepository).findById(1L);
        verifyNoMoreInteractions(playerRepository, shoeService);
    }
}
