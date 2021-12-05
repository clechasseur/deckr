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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        Game game = Mockito.mock(Game.class);
        Mockito.when(gameService.getGame(1L)).thenReturn(game);
        Mockito.when(shoeRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Shoe shoe = shoeService.createShoe(1L);

        Assertions.assertThat(shoe).isNotNull();
        Assertions.assertThat(shoe.getGame()).isEqualTo(game);
        Mockito.verify(gameService).getGame(1L);
        Mockito.verify(shoeRepository).save(Mockito.any(Shoe.class));
    }

    @Test
    public void getShoeWithNoShoeThrowsException() {
        Mockito.when(shoeRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> shoeService.getShoe(1L))
                .isInstanceOf(ShoeNotFoundException.class);
        Mockito.verify(shoeRepository).findById(1L);
    }

    @Test
    public void getShoeWithAShoeReturnsShoe() {
        Shoe shoe = Mockito.mock(Shoe.class);
        Mockito.when(shoeRepository.findById(1L)).thenReturn(Optional.of(shoe));

        Shoe actualShoe = shoeService.getShoe(1L);

        Assertions.assertThat(actualShoe).isNotNull();
        Assertions.assertThat(actualShoe).isEqualTo(shoe);
        Mockito.verify(shoeRepository).findById(1L);
    }

    @Test
    public void updateShoeReturnsUpdatedShoe() {
        Mockito.when(shoeRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Shoe shoe = Mockito.mock(Shoe.class);
        Shoe updatedShoe = shoeService.updateShoe(shoe);

        Assertions.assertThat(updatedShoe).isNotNull();
        Assertions.assertThat(updatedShoe).isEqualTo(shoe);
        Mockito.verify(shoeRepository).save(Mockito.any(Shoe.class));
    }

    @Test
    public void addDeckToShoeAdds52CardsToShoe() {
        Shoe shoe = new Shoe();
        Mockito.when(shoeRepository.findById(1L)).thenReturn(Optional.of(shoe));

        shoeService.addDeckToShoe(1L);

        Mockito.verify(shoeRepository).findById(1L);
        ArgumentCaptor<Shoe> shoeArgumentCaptor = ArgumentCaptor.forClass(Shoe.class);
        Mockito.verify(shoeRepository).save(shoeArgumentCaptor.capture());
        Shoe actualShoe = shoeArgumentCaptor.getValue();
        Assertions.assertThat(CardUtils.cardsAsList(actualShoe.getCards()).size()).isEqualTo(52);
    }

    @Test
    public void shuffleRandomizesTheCardsInShoe() {
        Shoe shoe = new Shoe();
        shoe.setCards("H1,H2,H3,H4,H5,H6");
        Mockito.when(shoeRepository.findById(1L)).thenReturn(Optional.of(shoe));

        shoeService.shuffle(1L);

        Mockito.verify(shoeRepository).findById(1L);
        ArgumentCaptor<Shoe> shoeArgumentCaptor = ArgumentCaptor.forClass(Shoe.class);
        Mockito.verify(shoeRepository).save(shoeArgumentCaptor.capture());
        Shoe actualShoe = shoeArgumentCaptor.getValue();
        Assertions.assertThat(actualShoe).isNotNull();
        Assertions.assertThat(actualShoe.getCards()).isNotEqualTo("H1,H2,H3,H4,H5,H6");
        Assertions.assertThat(CardUtils.cardsAsList(actualShoe.getCards()).size()).isEqualTo(6);
    }

    @Test
    public void shufflingAnEmptyShoeDoesNothing() {
        Shoe shoe = new Shoe();
        Mockito.when(shoeRepository.findById(1L)).thenReturn(Optional.of(shoe));

        shoeService.shuffle(1L);

        Mockito.verify(shoeRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(shoeRepository);
    }

    @Test
    public void gettingCountOfCardsLeftBySuitReturnsProperCounts() {
        Shoe shoe = new Shoe();
        shoe.setCards("H1,H2,H3,D4,D5,S6,S7,S8,S9,S10");
        Mockito.when(shoeRepository.findById(1L)).thenReturn(Optional.of(shoe));

        Map<Suit, Integer> counts = shoeService.getCountOfCardsLeftBySuit(1L);

        Assertions.assertThat(counts).isNotNull();
        Assertions.assertThat(counts).isEqualTo(Map.of(Suit.Hearths, 3, Suit.Diamonds, 2, Suit.Spades, 5));
        Mockito.verify(shoeRepository).findById(1L);
    }

    @Test
    public void gettingCardsLeftReturnsCardsProperlySorted() {
        Shoe shoe = new Shoe();
        shoe.setCards("H3,D10,D2,D13,S4,C7,C8,S3,S12,D1,H10,H9");
        Mockito.when(shoeRepository.findById(1L)).thenReturn(Optional.of(shoe));

        List<CardAndSuit> cards = shoeService.getCardsLeft(1L);

        Assertions.assertThat(cards).isNotNull();
        Assertions.assertThat(cards).isEqualTo(Arrays.asList(
                new CardAndSuit(Card.Ten, Suit.Hearths),
                new CardAndSuit(Card.Nine, Suit.Hearths),
                new CardAndSuit(Card.Three, Suit.Hearths),
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
        Mockito.verify(shoeRepository).findById(1L);
    }
}
