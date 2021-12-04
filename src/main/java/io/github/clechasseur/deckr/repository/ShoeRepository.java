package io.github.clechasseur.deckr.repository;

import io.github.clechasseur.deckr.model.Shoe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoeRepository extends JpaRepository<Shoe, Long> {
}
