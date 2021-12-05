package io.github.clechasseur.deckr;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.Player;
import io.github.clechasseur.deckr.model.Shoe;
import io.github.clechasseur.deckr.util.CardUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DeckrApplicationTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    public void contextLoads() {
    }

    @Test
    public void iWantToPlayAGame() throws Exception {
        MvcResult result =
                mockMvc.perform(post("/api/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test game\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        Game game = objectMapper.readValue(result.getResponse().getContentAsString(), Game.class);
        Long gameId = game.getId();

        mockMvc.perform(post("/api/game/" + gameId + "/shoe"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/game/" + gameId + "/shoe"))
                .andExpect(status().isNoContent());
        mockMvc.perform(patch("/api/game/" + gameId + "/shoe"))
                .andExpect(status().isNoContent());

        for (int i = 1; i <= 4; i++) {
            Player newPlayer = new Player();
            newPlayer.setGame(game);
            newPlayer.setName("Player " + i);
            result = mockMvc.perform(post("/api/player")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newPlayer)))
                    .andExpect(status().isCreated())
                    .andReturn();
            Player player = objectMapper.readValue(result.getResponse().getContentAsString(), Player.class);
            Long playerId = player.getId();

            mockMvc.perform(put("/api/player/" + playerId + "/hand")
                            .param("numCards", "5"))
                    .andExpect(status().isNoContent());

            result = mockMvc.perform(get("/api/player/" + playerId))
                    .andExpect(status().isOk())
                    .andReturn();
            player = objectMapper.readValue(result.getResponse().getContentAsString(), Player.class);
            assertThat(CardUtils.cardsAsList(player.getHand())).hasSize(5);
        }

        result = mockMvc.perform(get("/api/game/" + gameId + "/shoe"))
                .andExpect(status().isOk())
                .andReturn();
        Shoe shoe = objectMapper.readValue(result.getResponse().getContentAsString(), Shoe.class);
        assertThat(CardUtils.cardsAsList(shoe.getCards())).hasSize(52 - (4 * 5));
    }
}
