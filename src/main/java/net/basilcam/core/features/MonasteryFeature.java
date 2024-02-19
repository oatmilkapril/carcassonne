package net.basilcam.core.features;

import net.basilcam.core.Meeple;
import net.basilcam.core.Player;
import net.basilcam.core.PlayerManager;
import net.basilcam.core.tiles.Tile;
import net.basilcam.core.tiles.TileSection;
import net.basilcam.core.tiles.TileSectionType;

public class MonasteryFeature implements Feature {
    public static final int POINTS_PER_MONASTERY = 9;
    private static final int NUMBER_OF_ROWS = 3;
    private static final int NUMBER_OF_COLUMNS = 3;
    private final Tile[][] tiles;
    private final PlayerManager playerManager;

    private boolean hasBeenScored;

    public MonasteryFeature(PlayerManager playerManager, Tile tile) {
        this.playerManager = playerManager;
        this.tiles = new Tile[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        this.tiles[1][1] = tile;
        this.hasBeenScored = false;
    }

    public void addTile(Tile tile, int i, int j) {
        assert i >= 0 && i <= NUMBER_OF_ROWS : "invalid row index";
        assert j >= 0 && j <= NUMBER_OF_COLUMNS : "invalid column index";
        assert this.tiles[i][j] == null : "tile already set";

        this.tiles[i][j] = tile;
    }

    @Override
    public boolean isComplete() {
        for (int i = 0 ; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                if (this.tiles[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public TileSectionType getType() {
        return TileSectionType.MONASTERY;
    }

    @Override
    public void score() {
        if (!isComplete() || this.hasBeenScored) {
            return;
        }

        this.hasBeenScored = true;

        Tile tile = this.tiles[1][1];
        for (TileSection section : tile.getCenterSections()) { // there should only ever be one center section
            if (section.getMeeple().isPresent()) {
                Meeple meeple = section.getMeeple().get();
                Player player = this.playerManager.getMeepleOwner(meeple);
                section.removeMeeple();
                player.addScore(POINTS_PER_MONASTERY);
            }
        }
    }
}
