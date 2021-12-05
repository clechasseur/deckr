package io.github.clechasseur.deckr.service;

import io.github.clechasseur.deckr.exception.ShoeNotFoundException;
import io.github.clechasseur.deckr.model.Card;
import io.github.clechasseur.deckr.model.CardAndSuit;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.Shoe;
import io.github.clechasseur.deckr.model.Suit;
import io.github.clechasseur.deckr.repository.ShoeRepository;
import io.github.clechasseur.deckr.util.ArrayUtils;
import io.github.clechasseur.deckr.util.CardUtils;
import io.github.clechasseur.deckr.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShoeService {
    private final ShoeRepository shoeRepository;
    private final GameService gameService;

    public ShoeService(ShoeRepository shoeRepository, GameService gameService) {
        this.shoeRepository = shoeRepository;
        this.gameService = gameService;
    }

    public Shoe createShoe(Long gameId) {
        Game game = gameService.getGame(gameId);
        Shoe shoe = new Shoe();
        shoe.setGame(game);
        return shoeRepository.save(shoe);
    }

    public Shoe getShoe(Long id) {
        return shoeRepository.findById(id).orElseThrow(() -> new ShoeNotFoundException(id));
    }

    public void addDeckToShoe(Long shoeId) {
        Shoe shoe = getShoe(shoeId);
        String cards = StringUtils.orEmptyString(shoe.getCards());
        if (!cards.isEmpty()) {
            cards += ",";
        }
        cards += getStandardDeck();
        shoe.setCards(cards);
        shoeRepository.save(shoe);
    }

    public void shuffle(Long shoeId) {
        Shoe shoe = getShoe(shoeId);
        String[] cards = StringUtils.orEmptyString(shoe.getCards()).split(",");
        ArrayUtils.shuffleArray(cards);
        shoe.setCards(String.join(",", cards));
        shoeRepository.save(shoe);
    }

    public Map<Suit, Integer> getCountOfCardsLeftBySuit(Long shoeId) {
        Shoe shoe = getShoe(shoeId);
        Map<Suit, Integer> counts = new HashMap<>();
        CardUtils.cardsAsList(shoe.getCards()).stream()
                .map(CardAndSuit::parse)
                .map(CardAndSuit::getSuit)
                .forEach(suit -> counts.put(suit, counts.getOrDefault(suit, 0) + 1));
        return counts;
    }

    public List<CardAndSuit> getCardsLeft(Long shoeId) {
        Shoe shoe = getShoe(shoeId);
        return CardUtils.cardsAsList(shoe.getCards()).stream()
                .map(CardAndSuit::parse)
                .sorted(Comparator.comparing(CardAndSuit::getSuit)
                        .thenComparingInt(cs -> cs.getCard().getValue()).reversed())
                .collect(Collectors.toList());
    }

    private static String getStandardDeck() {
        return Arrays.stream(Suit.values())
                .map(ShoeService::getStandardSuit)
                .collect(Collectors.joining(","));
    }

    private static String getStandardSuit(Suit suit) {
        return Arrays.stream(Card.values())
                .map(c -> getStandardCard(suit, c))
                .collect(Collectors.joining(","));
    }

    private static String getStandardCard(Suit suit, Card card) {
        return new CardAndSuit(card, suit).toString();
    }
}
