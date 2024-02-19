package net.basilcam.core.features;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.basilcam.core.Meeple;
import net.basilcam.core.Player;
import net.basilcam.core.PlayerManager;
import net.basilcam.core.tiles.Tile;
import net.basilcam.core.tiles.TileManager;
import net.basilcam.core.tiles.TileSection;
import net.basilcam.core.tiles.TileSectionType;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.*;

public class GraphFeature implements Feature {
    @VisibleForTesting public static final int ROAD_POINTS_PER_TILE = 1;
    @VisibleForTesting public static final int CITY_POINTS_PER_TILE = 2;
    @VisibleForTesting public static final int COAT_OF_ARMS_POINTS_PER_TILE = 2;
    private final PlayerManager playerManager;
    private final TileManager tileManager;
    private final Map<TileSection, GraphFeatureNode> featureNodes;
    private final TileSectionType type;
    private boolean hasBeenScored;

    public GraphFeature(PlayerManager playerManager, TileManager tileManager, TileSectionType type) {
        this.playerManager = playerManager;
        this.tileManager = tileManager;
        this.featureNodes = new HashMap<>();
        this.type = type;
        this.hasBeenScored = false;
    }

    @Override
    public boolean isComplete() {
        for (GraphFeatureNode node : this.featureNodes.values()) {
            if (node.hasOpenConnection()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TileSectionType getType() {
        return this.type;
    }

    @Override
    public void score() {
        // todo: pretty inefficient, refactor to simplify

        if (!isComplete() || this.hasBeenScored) {
            return;
        }

        this.hasBeenScored = true;

        Set<Tile> tiles = new HashSet<>();
        Multimap<Player, Meeple> meeples = HashMultimap.create();
        for (TileSection tileSection : this.featureNodes.keySet()) {
            if (tileSection.getMeeple().isPresent()) {
                Meeple meeple = tileSection.getMeeple().get();
                Player owner = this.playerManager.getMeepleOwner(meeple);
                meeples.put(owner, meeple);

                tileSection.removeMeeple();
            }
            Tile tile = tileManager.getTileFromSection(tileSection);
            tiles.add(tile);
        }

        int score = this.type == TileSectionType.CITY
                ? tiles.size() * CITY_POINTS_PER_TILE + (int) tiles.stream().filter(Tile::hasCoatOfArms).count() * CITY_POINTS_PER_TILE
                : tiles.size() * ROAD_POINTS_PER_TILE;

        int maxMeepleCount = 0;
        List<Player> playersWithMaxMeepleCount = new ArrayList<>();
        for (Map.Entry<Player, Collection<Meeple>> entry : meeples.asMap().entrySet()) {
            if (entry.getValue().size() > maxMeepleCount) {
                maxMeepleCount = entry.getValue().size();
                playersWithMaxMeepleCount.clear();
                playersWithMaxMeepleCount.add(entry.getKey());
            } else if (entry.getValue().size() == maxMeepleCount) {
                playersWithMaxMeepleCount.add(entry.getKey());
            }
        }

        int scorePerPlayer = 0;
        if (!playersWithMaxMeepleCount.isEmpty()) {
            scorePerPlayer = score / playersWithMaxMeepleCount.size();
        }

        for (Player player : playersWithMaxMeepleCount) {
            player.addScore(scorePerPlayer);
        }
    }

    public void addNode(GraphFeatureNode node) {
        this.featureNodes.put(node.getTileSection(), node);
    }

    public GraphFeatureNode getNode(TileSection tileSection) {
        return this.featureNodes.get(tileSection);
    }

    public Collection<TileSection> getTileSections() {
        return this.featureNodes.keySet();
    }

    public void merge(Collection<? extends GraphFeature> graphFeatures) {
        for (GraphFeature feature : graphFeatures) {
            this.featureNodes.putAll(feature.featureNodes);
        }
    }

    public boolean canPlaceMeeple() {
        for (TileSection otherTileSection : this.featureNodes.keySet()) {
            if (otherTileSection.getMeeple().isPresent()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "GraphFeature{" +
                "featureNodes=" + featureNodes +
                ", type=" + type +
                '}';
    }
}
