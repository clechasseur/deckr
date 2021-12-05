package io.github.clechasseur.deckr;

import io.github.clechasseur.deckr.model.Game;
import io.github.clechasseur.deckr.model.Player;
import io.github.clechasseur.deckr.model.Shoe;
import io.github.clechasseur.deckr.repository.GameRepository;
import io.github.clechasseur.deckr.repository.PlayerRepository;
import io.github.clechasseur.deckr.repository.ShoeRepository;
import io.github.clechasseur.deckr.service.GameService;
import io.github.clechasseur.deckr.service.PlayerService;
import io.github.clechasseur.deckr.service.ShoeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeckrPOC {
    private static final Logger logger = LoggerFactory.getLogger(DeckrPOC.class);

    @Bean
    public CommandLineRunner loadPOC(
            GameService gameService,
            ShoeService shoeService,
            PlayerService playerService
    )
    {
        return args -> {
            Game game = gameService.createGame("Demo game");
            Shoe shoe = shoeService.createShoe(game.getId());
            shoeService.addDeckToShoe(shoe.getId());
            shoeService.shuffle(shoe.getId());
            Player player = playerService.createPlayer(game.getId(), "Player 1");
            playerService.dealCardsToPlayer(player.getId(), 5);
            logger.info("Player: " + playerService.getPlayer(player.getId()));
        };
    }
}
