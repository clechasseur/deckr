package io.github.clechasseur.deckr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.clechasseur.deckr.exception.GameNotFoundException;
import io.github.clechasseur.deckr.exception.PlayerNotFoundException;
import io.github.clechasseur.deckr.model.Card;
import io.github.clechasseur.deckr.model.CardAndSuit;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.Player;
import io.github.clechasseur.deckr.model.Suit;
import io.github.clechasseur.deckr.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createPlayerCallsService() throws Exception {
        when(playerService.createPlayer(any(), any())).thenAnswer(invocation -> {
            Player player = new Player();
            player.setId(3L);
            player.setName(invocation.getArgument(1));
            Game game = new Game();
            game.setId(invocation.getArgument(0));
            game.setName("Test game");
            game.setPlayers(Collections.singletonList(player));
            player.setGame(game);
            return player;
        });

        Game game = new Game();
        game.setId(1L);
        Player player = new Player();
        player.setId(3L);
        player.setName("Player 1");
        player.setGame(game);
        game.setPlayers(Collections.singletonList(player));
        mockMvc.perform(post("/api/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Player 1"))
                .andExpect(jsonPath("$.game.name").value("Test game"));

        verify(playerService).createPlayer(1L, "Player 1");
    }

    @Test
    public void getPlayerCallsService() throws Exception {
        Player player = new Player();
        player.setName("Player 1");
        when(playerService.getPlayer(3L)).thenReturn(player);

        mockMvc.perform(get("/api/player/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Player 1"));

        verify(playerService).getPlayer(3L);
    }

    @Test
    public void getPlayerOnNonExistentPlayerReturnsNotFound() throws Exception {
        when(playerService.getPlayer(3L)).thenThrow(new PlayerNotFoundException(3L));

        mockMvc.perform(get("/api/player/3"))
                .andExpect(status().isNotFound());

        verify(playerService).getPlayer(3L);
    }

    @Test
    public void deletePlayerCallsService() throws Exception {
        mockMvc.perform(delete("/api/player/3"))
                .andExpect(status().isNoContent());

        verify(playerService).deletePlayer(3L);
    }

    @Test
    public void getCardsCallsService() throws Exception {
        List<CardAndSuit> cards = Arrays.asList(
                new CardAndSuit(Card.King, Suit.Diamonds),
                new CardAndSuit(Card.Seven, Suit.Spades)
        );
        when(playerService.getCards(3L)).thenReturn(cards);

        mockMvc.perform(get("/api/player/3/hand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].card").value("King"))
                .andExpect(jsonPath("$[0].suit").value("Diamonds"))
                .andExpect(jsonPath("$[1].card").value("Seven"))
                .andExpect(jsonPath("$[1].suit").value("Spades"));

        verify(playerService).getCards(3L);
    }

    @Test
    public void dealCallsService() throws Exception {
        mockMvc.perform(put("/api/player/3/hand")
                        .param("numCards", "5"))
                .andExpect(status().isNoContent());

        verify(playerService).dealCards(3L, 5);
    }
}
