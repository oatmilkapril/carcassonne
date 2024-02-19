package net.basilcam.core;

import net.basilcam.core.tiles.TileSectionType;

import java.util.EnumSet;

public class Meeple {
    public static final EnumSet<TileSectionType> SUPPORTED_TYPES = EnumSet.of(TileSectionType.CITY, TileSectionType.ROAD, TileSectionType.MONASTERY);
    private boolean isPlaced;

    public Meeple() {
        this.isPlaced = false;
    }

    public boolean isPlaced() {
        return this.isPlaced;
    }

    public void placeMeeple() {
        this.isPlaced = true;
    }

    public void removeMeeple() {
        this.isPlaced = false;
    }
}
