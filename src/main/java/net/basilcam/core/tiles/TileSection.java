package net.basilcam.core.tiles;

import net.basilcam.core.Meeple;

import java.util.Optional;

public class TileSection {
    private Optional<Meeple> meeple;
    private final TileSectionType type;

    public TileSection(TileSectionType type) {
        this.meeple = Optional.empty();
        this.type = type;
    }

    public Optional<Meeple> getMeeple() {
        return meeple;
    }

    public void placeMeeple(Meeple meeple) {
        meeple.placeMeeple();
        this.meeple = Optional.of(meeple);
    }

    public void removeMeeple() {
        assert this.meeple.isPresent();
        this.meeple.get().removeMeeple();
        this.meeple = Optional.empty();
    }

    public TileSectionType getType() {
        return type;
    }
}
