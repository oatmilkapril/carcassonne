package net.basilcam.core.tiles;

import org.jetbrains.annotations.TestOnly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileManager {
    private final List<Tile> tiles;
    private final Tile startTile;
    protected Map<TileSection, Tile> sectionToTile;

    public TileManager() {
        this.startTile = TileStackFactory.createStartTile();
        this.tiles = TileStackFactory.createTileStack();
        this.sectionToTile = new HashMap<>();

        TileManager.addTileSectionMapping(this.sectionToTile, this.startTile);
        this.tiles.forEach(tile -> TileManager.addTileSectionMapping(this.sectionToTile, tile));
    }

    public static void addTileSectionMapping(Map<TileSection, Tile> sectionToTile, Tile tile) {
        for (TileSection tileSection : tile.getSections().values()) {
            sectionToTile.put(tileSection, tile);
        }
    }

    public Tile getStartTile() {
        return this.startTile;
    }

    public Tile drawTile() {
        assert hasMoreTiles();
        return this.tiles.remove(0);
    }

    public boolean hasMoreTiles() {
        return !this.tiles.isEmpty();
    }

    public Tile getTileFromSection(TileSection tileSection) {
        return this.sectionToTile.get(tileSection);
    }

    @TestOnly
    public void addTileSectionMapping(TileSection tileSection, Tile tile) {
        this.sectionToTile.put(tileSection, tile);
    }
}
