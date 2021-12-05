package io.github.clechasseur.deckr.service;

import io.github.clechasseur.deckr.exception.ShoeNotFoundException;
import io.github.clechasseur.deckr.model.Card;
import io.github.clechasseur.deckr.model.CardAndSuit;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.Shoe;
import io.github.clechasseur.deckr.model.Suit;
import io.github.clechasseur.deckr.repository.ShoeRepository;
import io.github.clechasseur.deckr.util.CardUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoeServiceTest {
    @InjectMocks
    private ShoeService shoeService;

    @Mock
    private ShoeRepository shoeRepository;

    @Mock
    private GameService gameService;

    @Test
    public void createShoeReturnsNewShoe() {
        Game game = mock(Game.class);
        when(gameService.getGame(1L)).thenReturn(game);
        when(shoeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Shoe shoe = shoeService.createShoe(1L);

        Assertions.assertThat(shoe).isNotNull();
        Assertions.assertThat(shoe.getGame()).isEqualTo(game);
        verify(gameService).getGame(1L);
        verify(shoeRepository).save(any(Shoe.class));
    }

    @Test
    public void getShoeWithNoShoeThrowsException() {
        when(shoeRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> shoeService.getShoe(1L))
                .isInstanceOf(ShoeNotFoundException.class);
        verify(shoeRepository).findById(1L);
    }

    @Test
    public void getShoeWithAShoeReturnsShoe() {
        Shoe shoe = mock(Shoe.class);
        when(shoeRepository.findById(1L)).thenReturn(Optional.of(shoe));

        Shoe actualShoe = shoeService.getShoe(1L);

        Assertions.assertThat(actualShoe).isNotNull();
        Assertions.assertThat(actualShoe).isEqualTo(shoe);
        verify(shoeRepository).findById(1L);
    }

    @Test
    public void updateShoeReturnsUpdatedShoe() {
        when(shoeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Shoe shoe = mock(Shoe.class);
        Shoe updatedShoe = shoeService.updateShoe(shoe);

        Assertions.assertThat(updatedShoe).isNotNull();
        Assertions.assertThat(updatedShoe).isEqualTo(shoe);
        verify(shoeRepository).save(any(Shoe.class));
    }

    @Test
    public void addDeckToShoeAdds52CardsToShoe() {
        Shoe shoe = new Shoe();
        when(shoeRepository.findById(1L)).thenReturn(Optional.of(shoe));

        shoeService.addDeckToShoe(1L);

        verify(shoeRepository).findById(1L);
        ArgumentCaptor<Shoe> shoeArgumentCaptor = ArgumentCaptor.forClass(Shoe.class);
        verify(shoeRepository).save(shoeArgumentCaptor.capture());
        Shoe actualShoe = shoeArgumentCaptor.getValue();
        Assertions.assertThat(CardUtils.cardsAsList(actualShoe.getCards()).size()).isEqualTo(52);
    }

    @Test
    public void shuffleRandomizesTheCardsInShoe() {
        Shoe shoe = new Shoe();
        shoe.setCards("H1,H2,H3,H4,H5,H6");
        when(shoeRepository.findById(1L)).thenReturn(Optional.of(shoe));

        shoeService.shuffle(1L);

        verify(shoeRepository).findById(1L);
        ArgumentCaptor<Shoe> shoeArgumentCaptor = ArgumentCaptor.forClass(Shoe.class);
        verify(shoeRepository).save(shoeArgumentCaptor.capture());
        Shoe actualShoe = shoeArgumentCaptor.getValue();
        Assertions.assertThat(actualShoe).isNotNull();
        Assertions.assertThat(actualShoe.getCards()).isNotEqualTo("H1,H2,H3,H4,H5,H6");
        Assertions.assertThat(CardUtils.cardsAsList(actualShoe.getCards()).size()).isEqualTo(6);
    }

    @Test
    public void shufflingAnEmptyShoeDoesNothing() {
        Shoe shoe = new Shoe();
        when(shoeRepository.findById(1L)).thenReturn(Optional.of(shoe));

        shoeService.shuffle(1L);

        verify(shoeRepository).findById(1L);
        verifyNoMoreInteractions(shoeRepository);
    }

    @Test
    public void gettingCountOfCardsLeftBySuitReturnsProperCounts() {
        Shoe shoe = new Shoe();
        shoe.setCards("H1,H2,H3,D4,D5,S6,S7,S8,S9,S10");
        when(shoeRepository.findById(1L)).thenReturn(Optional.of(shoe));

        Map<Suit, Integer> counts = shoeService.getCountOfCardsLeftBySuit(1L);

        Assertions.assertThat(counts).isNotNull();
        Assertions.assertThat(counts).isEqualTo(Map.of(Suit.Hearts, 3, Suit.Diamonds, 2, Suit.Spades, 5));
        verify(shoeRepository).findById(1L);
    }

    @Test
    public void gettingCardsLeftReturnsCardsProperlySorted() {
        Shoe shoe = new Shoe();
        shoe.setCards("H3,D10,D2,D13,S4,C7,C8,S3,S12,D1,H10,H9");
        when(shoeRepository.findById(1L)).thenReturn(Optional.of(shoe));

        List<CardAndSuit> cards = shoeService.getCardsLeft(1L);

        Assertions.assertThat(cards).isNotNull();
        Assertions.assertThat(cards).isEqualTo(Arrays.asList(
                new CardAndSuit(Card.Ten, Suit.Hearts),
                new CardAndSuit(Card.Nine, Suit.Hearts),
                new CardAndSuit(Card.Three, Suit.Hearts),
                new CardAndSuit(Card.Queen, Suit.Spades),
                new CardAndSuit(Card.Four, Suit.Spades),
                new CardAndSuit(Card.Three, Suit.Spades),
                new CardAndSuit(Card.Eight, Suit.Clubs),
                new CardAndSuit(Card.Seven, Suit.Clubs),
                new CardAndSuit(Card.King, Suit.Diamonds),
                new CardAndSuit(Card.Ten, Suit.Diamonds),
                new CardAndSuit(Card.Two, Suit.Diamonds),
                new CardAndSuit(Card.Ace, Suit.Diamonds)
        ));
        verify(shoeRepository).findById(1L);
    }
}
