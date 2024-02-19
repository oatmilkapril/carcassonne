package net.basilcam.core.features;

import net.basilcam.core.Board;
import net.basilcam.core.Pair;
import net.basilcam.core.PlayerManager;
import net.basilcam.core.tiles.Tile;
import net.basilcam.core.tiles.TileSection;
import net.basilcam.core.tiles.TileSectionType;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.*;
import java.util.stream.Collectors;

public class MonasteryFeatureManager implements FeatureManager {
    private final PlayerManager playerManager;
    private final Board board;
    private final Map<Tile, MonasteryFeature> centerTileToFeature;

    public MonasteryFeatureManager(PlayerManager playerManager, Board board) {
        this.playerManager = playerManager;
        this.board = board;
        this.centerTileToFeature = new HashMap<>();

        this.board.forEachTile(this::updateFeatures);
    }

    @Override
    public void updateFeatures(Tile tile, int xPosition, int yPosition) {
        // todo: combine these two pairs of nested loops -- i think its possible

        // add tile to surrounding monasteries
        for (int x = xPosition - 1; x <= xPosition + 1; x++) {
            for (int y = yPosition - 1; y <= yPosition + 1; y++) {
                if (x == xPosition && y == yPosition) {
                    continue;
                }

                Optional<Tile> centerTile = this.board.getTile(x, y);
                if (centerTile.isEmpty()) {
                    continue;
                }

                MonasteryFeature feature = this.centerTileToFeature.get(centerTile.get());
                if (feature == null) {
                    continue;
                }

                Pair<Integer> indices = convertPositionsToIndices(new Pair<>(x, y),
                        new Pair<>(xPosition, yPosition));
                feature.addTile(tile, indices.getFirst(), indices.getSecond());
            }
        }

        // if tile contains a monastery, add a new feature, add surrounding tiles
        if (MonasteryFeatureManager.containsMonastery(tile)) {
            MonasteryFeature feature = new MonasteryFeature(this.playerManager, tile);
            this.centerTileToFeature.put(tile, feature);

            for (int x = xPosition - 1; x <= xPosition + 1; x++) {
                for (int y = yPosition - 1; y <= yPosition + 1; y++) {
                    if (x == xPosition && y == yPosition) {
                        continue;
                    }

                    Optional<Tile> adjacentTile = this.board.getTile(x, y);
                    if (adjacentTile.isPresent()) {
                        Pair<Integer> indices = convertPositionsToIndices(new Pair<>(xPosition, yPosition),
                                new Pair<>(x, y));
                        feature.addTile(adjacentTile.get(), indices.getFirst(), indices.getSecond());
                    }
                }
            }
        }
    }

    @Override
    public Collection<? extends Feature> getFeatures() {
        return this.centerTileToFeature.values();
    }

    @Override
    public boolean canPlaceMeeple(Tile tile, TileSection section) {
        return true;
    }

    @Override
    public void scoreFeatures() {
        // todo: very inefficient
        new HashSet<>(this.centerTileToFeature.values()).forEach(MonasteryFeature::score);
    }

    @VisibleForTesting
    public static Pair<Integer> convertPositionsToIndices(Pair<Integer> monasteryXY, Pair<Integer> tileXY) {
        int i = 1 + (monasteryXY.getSecond() - tileXY.getSecond());
        int j = 1 + (tileXY.getFirst() - monasteryXY.getFirst());

        return new Pair<>(i, j);
    }

    private static boolean containsMonastery(Tile tile) {
        return tile.getCenterSections().stream()
                .map(TileSection::getType)
                .collect(Collectors.toList())
                .contains(TileSectionType.MONASTERY);
    }
}
