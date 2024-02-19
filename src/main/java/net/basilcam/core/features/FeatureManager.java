package net.basilcam.core.features;

import net.basilcam.core.tiles.Tile;
import net.basilcam.core.tiles.TileSection;

import java.util.Collection;

public interface FeatureManager {

    void updateFeatures(Tile tile, int xPosition, int yPosition);

    Collection<? extends Feature> getFeatures();

    boolean canPlaceMeeple(Tile tile, TileSection section);

    void scoreFeatures();
}
