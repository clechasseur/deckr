package io.github.clechasseur.deckr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Objects;

@Entity
public class Shoe {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "gameId", nullable = false)
    private Game game;

    @Column
    private String cards;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shoe shoe = (Shoe) o;
        return id.equals(shoe.id) && game.getId().equals(shoe.game.getId()) && Objects.equals(cards, shoe.cards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, game.getId(), cards);
    }

    @Override
    public String toString() {
        return "Shoe{" +
                "id=" + id +
                ", gameId=" + game.getId() +
                ", cards='" + cards + '\'' +
                '}';
    }
}
