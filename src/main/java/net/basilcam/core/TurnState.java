package net.basilcam.core;

import net.basilcam.core.tiles.Tile;

public class TurnState {
    private final Tile tile;
    private boolean hasPlacedMeeple;
    private boolean hasScored;
    private boolean hasPlacedTile;

    public TurnState(Tile tile) {
        this.tile = tile;
        this.hasPlacedMeeple = false;
        this.hasScored = false;
        this.hasPlacedTile = false;
    }

    public Tile getTile() {
        return this.tile;
    }

    public boolean hasPlacedTile() {
        return this.hasPlacedTile;
    }

    public void placedTile() {
        this.hasPlacedTile = true;
    }

    public boolean hasPlacedMeeple() {
        return this.hasPlacedMeeple;
    }

    public void placedMeeple() {
        this.hasPlacedMeeple = true;
    }

    public boolean hasScored() {
        return this.hasScored;
    }

    public void scored() {
        this.hasScored = true;
    }
}
