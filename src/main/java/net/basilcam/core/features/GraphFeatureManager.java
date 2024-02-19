package net.basilcam.core.features;

import net.basilcam.core.Board;
import net.basilcam.core.Direction;
import net.basilcam.core.PlayerManager;
import net.basilcam.core.tiles.Tile;
import net.basilcam.core.tiles.TileManager;
import net.basilcam.core.tiles.TileSection;
import net.basilcam.core.tiles.TileSectionType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GraphFeatureManager implements FeatureManager {
    private final PlayerManager playerManager;
    private final TileManager tileManager;
    private final Board board;
    private final Map<TileSection, GraphFeature> tileSectionToFeature;

    GraphFeatureManager(PlayerManager playerManager, TileManager tileManager, Board board) {
        this.playerManager = playerManager;
        this.tileManager = tileManager;
        this.board = board;
        this.tileSectionToFeature = new HashMap<>();

        this.board.forEachTile(this::updateFeatures);
    }

    @Override
    public void updateFeatures(Tile tile, int xPosition, int yPosition) {
        updateFeaturesForEdges(tile, xPosition, yPosition, Direction.UP);
        updateFeaturesForEdges(tile, xPosition, yPosition, Direction.LEFT);
        updateFeaturesForEdges(tile, xPosition, yPosition, Direction.DOWN);
        updateFeaturesForEdges(tile, xPosition, yPosition, Direction.RIGHT);
        updateFeaturesForCenter(tile);
    }

    @Override
    public Collection<GraphFeature> getFeatures() {
        return Set.copyOf(this.tileSectionToFeature.values());
    }

    @Override
    public boolean canPlaceMeeple(Tile tile, TileSection section) {
        if (!isSupportedFeatureType(section.getType())) {
            return true;
        }

        @Nullable GraphFeature feature = this.tileSectionToFeature.get(section);
        if (feature == null) {
            return true;
        }
        return feature.canPlaceMeeple();
    }

    @Override
    public void scoreFeatures() {
        // todo: very inefficient
        new HashSet<>(this.tileSectionToFeature.values()).forEach(GraphFeature::score);
    }

    private void updateFeaturesForEdges(Tile tile, int xPosition, int yPosition, Direction direction) {
        TileSection tileSection = tile.getSection(direction);

        if (!isSupportedFeatureType(tileSection.getType())) {
            return;
        }

        GraphFeatureNode newNode = new GraphFeatureNode(tileSection);
        for (Direction closedDirections : direction.perpendicularDirections()) {
            newNode.closeNode(closedDirections);
        }

        Optional<Tile> abuttingTile = this.board.getAbuttingTile(xPosition, yPosition, direction);
        if (abuttingTile.isPresent()) {
            TileSection abuttingSection = abuttingTile.get().getSection(direction.oppositeDirection());
            assert abuttingSection.getType() == tileSection.getType() : "tile placement is invalid";

            GraphFeature abuttingFeature = this.tileSectionToFeature.get(abuttingSection);
            assert abuttingFeature != null : "no feature found for section";

            connectAbuttingNode(abuttingFeature, newNode, abuttingSection, direction.oppositeDirection());
            this.tileSectionToFeature.put(tileSection, abuttingFeature);
        } else {
            GraphFeature feature = new GraphFeature(playerManager, tileManager, tileSection.getType());
            feature.addNode(newNode);
            this.tileSectionToFeature.put(tileSection, feature);
        }
    }

    private void updateFeaturesForCenter(Tile tile) {
        for (TileSection centerSection : tile.getCenterSections()) {
            GraphFeatureNode centerNode = new GraphFeatureNode(centerSection);

            if (!isSupportedFeatureType(centerSection.getType())) {
                continue;
            }

            List<GraphFeature> connectedFeatures = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                TileSection adjacentTileSection = tile.getSection(direction);
                if (adjacentTileSection.getType() != centerSection.getType()) {
                    centerNode.closeNode(direction);
                    continue;
                }

                GraphFeature adjacentFeature = this.tileSectionToFeature.get(adjacentTileSection);
                assert adjacentFeature != null : "unexpected missing feature";

                connectCenterNode(adjacentFeature, centerNode, adjacentTileSection, direction.oppositeDirection());

                connectedFeatures.add(adjacentFeature);
            }

            if (connectedFeatures.size() == 0) {
                continue;
            }

            if (connectedFeatures.size() == 1) {
                this.tileSectionToFeature.put(centerSection, connectedFeatures.get(0));
                continue;
            }

            GraphFeature mergedFeature = new GraphFeature(this.playerManager, this.tileManager, centerSection.getType());
            mergedFeature.merge(connectedFeatures);
            for (TileSection tileSection : mergedFeature.getTileSections()) {
                this.tileSectionToFeature.put(tileSection, mergedFeature);
            }
        }

        for (Direction direction : Direction.values()) {
            TileSection tileSection = tile.getSection(direction);
            GraphFeature feature = this.tileSectionToFeature.get(tileSection);
            if (feature == null) {
                continue;
            }
            GraphFeatureNode node = feature.getNode(tileSection);
            if (node.getNode(direction.oppositeDirection()) == GraphFeatureNode.OPEN_NODE) {
                node.closeNode(direction.oppositeDirection());
            }
        }
    }

    private void connectAbuttingNode(GraphFeature feature,
                                     GraphFeatureNode newNode,
                                     TileSection existingSection,
                                     Direction directionFromExisting) {
        GraphFeatureNode existingNode = feature.getNode(existingSection);
        feature.addNode(newNode);

        existingNode.connectNode(newNode, directionFromExisting);
        newNode.connectNode(existingNode, directionFromExisting.oppositeDirection());
    }

    private void connectCenterNode(GraphFeature feature,
                                   GraphFeatureNode centerNode,
                                   TileSection existingSection,
                                   Direction directionFromExisting) {
        GraphFeatureNode existingNode = feature.getNode(existingSection);

        assert existingNode != null : "unexpected null node";

        existingNode.connectNode(centerNode, directionFromExisting);
        centerNode.connectNode(existingNode, directionFromExisting.oppositeDirection());

        feature.addNode(centerNode);
    }

    private boolean isSupportedFeatureType(TileSectionType type) {
        return type == TileSectionType.CITY
                || type == TileSectionType.ROAD;
    }
}