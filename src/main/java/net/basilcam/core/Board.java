package net.basilcam.core;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import net.basilcam.core.tiles.Tile;

import java.util.Optional;

public class Board {
    private final Table<Integer, Integer, Tile> tiles;

    // up is +y
    // down is -y
    // left is -x
    // right is +x

    public Board(Tile startTile) {
        this.tiles = HashBasedTable.create();
        this.tiles.put(0, 0, startTile);
    }

    public Optional<Tile> getTile(int xPosition, int yPosition) {
        return Optional.ofNullable(tiles.get(xPosition, yPosition));
    }

    public Optional<Tile> getAbuttingTile(int xPosition, int yPosition, Direction direction) {
        switch (direction) {
            case UP:
                return getTile(xPosition, yPosition + 1);
            case LEFT:
                return getTile(xPosition - 1, yPosition);
            case DOWN:
                return getTile(xPosition, yPosition - 1);
            case RIGHT:
                return getTile(xPosition + 1, yPosition);
            default:
                return Optional.empty();
        }
    }

    public void placeTile(Tile tile, int xPosition, int yPosition) {
        this.tiles.put(xPosition, yPosition, tile);
    }

    public ImmutableTable<Integer, Integer, Tile> getTiles() {
        return ImmutableTable.copyOf(tiles);
    }

    public void forEachTile(TileConsumer consumer) {
        for (Table.Cell<Integer, Integer, Tile> cell : this.tiles.cellSet()) {
            consumer.accept(cell.getValue(), cell.getRowKey(), cell.getColumnKey());
        }
    }

    public interface TileConsumer {
        void accept(Tile tile, int xPosition, int yPosition);
    }
}
