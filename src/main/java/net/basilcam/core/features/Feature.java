package net.basilcam.core.features;

import net.basilcam.core.tiles.TileSectionType;

public interface Feature {
    boolean isComplete();
    TileSectionType getType();
    void score();
}
