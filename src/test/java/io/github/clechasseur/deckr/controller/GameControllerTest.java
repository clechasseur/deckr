package io.github.clechasseur.deckr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.clechasseur.deckr.exception.GameNotFoundException;
import io.github.clechasseur.deckr.model.Card;
import io.github.clechasseur.deckr.model.CardAndSuit;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.Player;
import io.github.clechasseur.deckr.model.PlayerAndValue;
import io.github.clechasseur.deckr.model.Shoe;
import io.github.clechasseur.deckr.model.Suit;
import io.github.clechasseur.deckr.service.GameService;
import io.github.clechasseur.deckr.service.ShoeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(GameController.class)
public class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private ShoeService shoeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createGameCallsService() throws Exception {
        when(gameService.createGame(any())).thenAnswer(invocation -> {
            Game game = new Game();
            game.setName(invocation.getArgument(0));
            return game;
        });

        Game game = new Game();
        game.setName("Test game");
        mockMvc.perform(post("/api/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(game)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test game"));

        verify(gameService).createGame("Test game");
    }

    @Test
    public void getGameCallsService() throws Exception {
        Game game = new Game();
        game.setName("Test game");
        when(gameService.getGame(1L)).thenReturn(game);

        mockMvc.perform(get("/api/game/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test game"));

        verify(gameService).getGame(1L);
    }

    @Test
    public void getGameOnNonExistentGameReturnsNotFound() throws Exception {
        when(gameService.getGame(1L)).thenThrow(new GameNotFoundException(1L));

        mockMvc.perform(get("/api/game/1"))
                .andExpect(status().isNotFound());

        verify(gameService).getGame(1L);
    }

    @Test
    public void deleteGameCallsService() throws Exception {
        mockMvc.perform(delete("/api/game/1"))
                .andExpect(status().isNoContent());

        verify(gameService).deleteGame(1L);
    }

    @Test
    public void createShoeCallsService() throws Exception {
        Game game = new Game();
        game.setId(1L);
        game.setName("Test game");
        Shoe shoe = new Shoe();
        shoe.setId(2L);
        shoe.setGame(game);
        game.setShoe(shoe);
        when(shoeService.createShoe(1L)).thenReturn(shoe);

        mockMvc.perform(post("/api/game/1/shoe"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.game.name").value("Test game"));

        verify(shoeService).createShoe(1L);
    }

    @Test
    public void getShoeCallsService() throws Exception {
        Game game = new Game();
        game.setId(1L);
        game.setName("Test game");
        Shoe shoe = new Shoe();
        shoe.setId(2L);
        shoe.setGame(game);
        game.setShoe(shoe);
        when(gameService.getGame(1L)).thenReturn(game);
        when(shoeService.getShoe(2L)).thenReturn(shoe);

        mockMvc.perform(get("/api/game/1/shoe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.game.name").value("Test game"));

        verify(gameService).getGame(1L);
        verify(shoeService).getShoe(2L);
    }

    @Test
    public void getShoeOnAGameWithoutShoeReturnsPreconditionFailed() throws Exception {
        Game game = new Game();
        game.setId(1L);
        when(gameService.getGame(1L)).thenReturn(game);

        mockMvc.perform(get("/api/game/1/shoe"))
                .andExpect(status().isPreconditionFailed());

        verify(gameService).getGame(1L);
        verifyNoInteractions(shoeService);
    }

    @Test
    public void addDeckToShoeCallsService() throws Exception {
        Game game = new Game();
        game.setId(1L);
        game.setName("Test game");
        Shoe shoe = new Shoe();
        shoe.setId(2L);
        shoe.setGame(game);
        game.setShoe(shoe);
        when(gameService.getGame(1L)).thenReturn(game);

        mockMvc.perform(put("/api/game/1/shoe"))
                .andExpect(status().isNoContent());

        verify(gameService).getGame(1L);
        verify(shoeService).addDeckToShoe(2L);
    }

    @Test
    public void shuffleShoeCallsService() throws Exception {
        Game game = new Game();
        game.setId(1L);
        game.setName("Test game");
        Shoe shoe = new Shoe();
        shoe.setId(2L);
        shoe.setGame(game);
        game.setShoe(shoe);
        when(gameService.getGame(1L)).thenReturn(game);

        mockMvc.perform(patch("/api/game/1/shoe"))
                .andExpect(status().isNoContent());

        verify(gameService).getGame(1L);
        verify(shoeService).shuffle(2L);
    }

    @Test
    public void getCountOfCardsLeftInShoeBySuitCallsService() throws Exception {
        Game game = new Game();
        game.setId(1L);
        game.setName("Test game");
        Shoe shoe = new Shoe();
        shoe.setId(2L);
        shoe.setGame(game);
        game.setShoe(shoe);
        Map<Suit, Integer> suits = Map.of(Suit.Hearts, 10, Suit.Clubs, 8, Suit.Diamonds, 2);
        when(gameService.getGame(1L)).thenReturn(game);
        when(shoeService.getCountOfCardsLeftBySuit(2L)).thenReturn(suits);

        mockMvc.perform(get("/api/game/1/shoe/suits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Hearts").value(10))
                .andExpect(jsonPath("$.Clubs").value(8))
                .andExpect(jsonPath("$.Diamonds").value(2));

        verify(gameService).getGame(1L);
        verify(shoeService).getCountOfCardsLeftBySuit(2L);
    }

    @Test
    public void getCardsLeftInShoeCallsService() throws Exception {
        Game game = new Game();
        game.setId(1L);
        game.setName("Test game");
        Shoe shoe = new Shoe();
        shoe.setId(2L);
        shoe.setGame(game);
        game.setShoe(shoe);
        List<CardAndSuit> cards = Arrays.asList(
                new CardAndSuit(Card.Ten, Suit.Clubs),
                new CardAndSuit(Card.Ace, Suit.Spades)
        );
        when(gameService.getGame(1L)).thenReturn(game);
        when(shoeService.getCardsLeft(2L)).thenReturn(cards);

        mockMvc.perform(get("/api/game/1/shoe/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].card").value("Ten"))
                .andExpect(jsonPath("$[0].suit").value("Clubs"))
                .andExpect(jsonPath("$[1].card").value("Ace"))
                .andExpect(jsonPath("$[1].suit").value("Spades"));

        verify(gameService).getGame(1L);
        verify(shoeService).getCardsLeft(2L);
    }

    @Test
    public void getPlayersAndValuesCallsService() throws Exception {
        Player player1 = new Player();
        player1.setName("Player 1");
        Player player2 = new Player();
        player2.setName("Player 2");
        List<PlayerAndValue> players = Arrays.asList(
                new PlayerAndValue(player1, 42),
                new PlayerAndValue(player2, 23)
        );
        when(gameService.getPlayersAndValues(1L)).thenReturn(players);

        mockMvc.perform(get("/api/game/1/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].player.name").value("Player 1"))
                .andExpect(jsonPath("$[0].value").value(42))
                .andExpect(jsonPath("$[1].player.name").value("Player 2"))
                .andExpect(jsonPath("$[1].value").value(23));

        verify(gameService).getPlayersAndValues(1L);
    }
}
