package io.github.clechasseur.deckr.repository;

import io.github.clechasseur.deckr.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
