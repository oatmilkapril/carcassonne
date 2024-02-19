package net.basilcam.core;

import net.basilcam.gui.PlayerColor;

import java.util.*;

public class Player {
    private static final int MEEPLE_PER_PLAYER = 7;

    private final String name;
    private int score;
    private final Meeple[] meeples;
    private final PlayerColor color;

    public Player(String name, PlayerColor color) {
        this.name = name;
        this.score = 0;
        this.meeples = Player.createMeeples();
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public PlayerColor getColor() {
        return this.color;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public int getScore() {
        return this.score;
    }

    public Optional<Meeple> getMeeple() {
        for (Meeple meeple : this.meeples) {
            if (!meeple.isPlaced()) {
                return Optional.of(meeple);
            }
        }
        return Optional.empty();
    }

    public Collection<Meeple> getMeeples() {
        return Arrays.asList(this.meeples);
    }

    public long getNumberOfUnplacedMeeples() {
        return Arrays.stream(this.meeples).filter(meeple -> !meeple.isPlaced()).count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return this.score == player.score
                && Objects.equals(this.name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.score);
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", meeples=" + Arrays.toString(meeples) +
                '}';
    }

    private static Meeple[] createMeeples() {
        Meeple[] meeples = new Meeple[MEEPLE_PER_PLAYER];
        for (int i = 0; i < MEEPLE_PER_PLAYER; i++) {
            meeples[i] = new Meeple();
        }
        return meeples;
    }
}
