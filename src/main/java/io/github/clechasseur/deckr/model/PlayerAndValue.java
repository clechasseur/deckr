package io.github.clechasseur.deckr.model;

import java.util.Objects;

public class PlayerAndValue {
    private Player player;
    private int value;

    public PlayerAndValue(Player player, int value) {
        this.player = player;
        this.value = value;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerAndValue that = (PlayerAndValue) o;
        return value == that.value && Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, value);
    }

    @Override
    public String toString() {
        return "PlayerAndValue{" +
                "player=" + player +
                ", value=" + value +
                '}';
    }
}
