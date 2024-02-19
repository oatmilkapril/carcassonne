package net.basilcam.core.features;

import com.google.common.collect.ImmutableMap;
import net.basilcam.core.*;
import net.basilcam.core.tiles.Tile;
import net.basilcam.core.tiles.TileSectionType;
import net.basilcam.core.tiles.*;
import net.basilcam.gui.PlayerColor;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class MonasteryFeatureManagerTest {
    private PlayerManager playerManager;
    private TestTileManager tileManager;
    private Board board;
    private MonasteryFeatureManager featureManager;
    private Player player;

    @BeforeEach
    public void beforeEach() {
        this.playerManager = new PlayerManager();
        this.tileManager = new TestTileManager();
        this.board = new Board(this.tileManager.getStartTile());
        this.featureManager = new MonasteryFeatureManager(this.playerManager, this.board);

        this.player = this.playerManager.addPlayer("cam", PlayerColor.RED);
    }

    @Test
    public void shouldInitiallyHaveNoFeatures() {
        assertFeatureCount(0);
    }

    @Test
    public void shouldAddFeatureWhenPlacingMonastery() {
        Tile tile18 = tileManager.drawTileById(18);
        tile18.rotateClockwise();

        placeTileAndUpdate(tile18, 1, 0);

        assertFeatureCount(1);
        assertFeature(TileSectionType.MONASTERY, 1, false);
    }

    @ParameterizedTest
    @ValueSource(booleans= {true, false})
    public void shouldCompleteMonastery_completedByMonasteryTile(boolean shouldPlaceMeeple) {
        Tile tile1 = tileManager.drawTileById(1);
        placeTileAndUpdate(tile1, 0, 1);
        assertFeatureCount(0);

        Tile tile20 = tileManager.drawTileById(20);
        tile20.rotateClockwise();
        tile20.rotateClockwise();
        tile20.rotateClockwise();
        placeTileAndUpdate(tile20, 1, 1);
        assertFeatureCount(0);

        Tile tile10 = tileManager.drawTileById(10);
        placeTileAndUpdate(tile10, 2, 1);
        assertFeatureCount(0);

        Tile tile15 = tileManager.drawTileById(15);
        tile15.rotateClockwise();
        tile15.rotateClockwise();
        placeTileAndUpdate(tile15, 2, 0);
        assertFeatureCount(0);

        Tile tile10_2 = tileManager.drawTileById(10);
        tile10_2.rotateClockwise();
        placeTileAndUpdate(tile10_2, 0, -1);
        assertFeatureCount(0);

        Tile tile10_3 = tileManager.drawTileById(10);
        tile10_3.rotateClockwise();
        placeTileAndUpdate(tile10_3, 1, -1);
        assertFeatureCount(0);

        Tile tile10_4 = tileManager.drawTileById(10);
        tile10_4.rotateClockwise();
        placeTileAndUpdate(tile10_4, 2, -1);
        assertFeatureCount(0);

        Tile tile18 = tileManager.drawTileById(18);
        tile18.rotateClockwise();
        placeTileAndUpdate(tile18, 1, 0);
        if (shouldPlaceMeeple) {
            TileSection monasterySection = tile18.getCenterSections().get(0);
            placeMeeple(monasterySection);
        }
        assertFeatureCount(1);
        assertFeature(TileSectionType.MONASTERY, 1, true);

        this.featureManager.scoreFeatures();

        if (shouldPlaceMeeple) {
            assertThat(this.player.getScore()).isEqualTo(MonasteryFeature.POINTS_PER_MONASTERY);
        } else {
            assertThat(this.player.getScore()).isEqualTo(0);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans= {true, false})
    public void shouldCompleteMonastery_completedByAnotherTile(boolean shouldPlaceMeeple) {
        Tile tile1 = tileManager.drawTileById(1);
        placeTileAndUpdate(tile1, 0, 1);
        assertFeatureCount(0);

        Tile tile18 = tileManager.drawTileById(18);
        tile18.rotateClockwise();
        placeTileAndUpdate(tile18, 1, 0);
        if (shouldPlaceMeeple) {
            TileSection monasterySection = tile18.getCenterSections().get(0);
            placeMeeple(monasterySection);
        }
        assertFeatureCount(1);
        assertFeature(TileSectionType.MONASTERY, 1, false);

        Tile tile20 = tileManager.drawTileById(20);
        tile20.rotateClockwise();
        tile20.rotateClockwise();
        tile20.rotateClockwise();
        placeTileAndUpdate(tile20, 1, 1);
        assertFeatureCount(1);
        assertFeature(TileSectionType.MONASTERY, 1, false);

        Tile tile10 = tileManager.drawTileById(10);
        placeTileAndUpdate(tile10, 2, 1);
        assertFeatureCount(1);
        assertFeature(TileSectionType.MONASTERY, 1, false);

        Tile tile15 = tileManager.drawTileById(15);
        tile15.rotateClockwise();
        tile15.rotateClockwise();
        placeTileAndUpdate(tile15, 2, 0);
        assertFeatureCount(1);
        assertFeature(TileSectionType.MONASTERY, 1, false);

        Tile tile10_2 = tileManager.drawTileById(10);
        tile10_2.rotateClockwise();
        placeTileAndUpdate(tile10_2, 0, -1);
        assertFeatureCount(1);
        assertFeature(TileSectionType.MONASTERY, 1, false);

        Tile tile10_3 = tileManager.drawTileById(10);
        tile10_3.rotateClockwise();
        placeTileAndUpdate(tile10_3, 1, -1);
        assertFeatureCount(1);
        assertFeature(TileSectionType.MONASTERY, 1, false);

        Tile tile10_4 = tileManager.drawTileById(10);
        tile10_4.rotateClockwise();
        placeTileAndUpdate(tile10_4, 2, -1);
        assertFeatureCount(1);
        assertFeature(TileSectionType.MONASTERY, 1, true);

        this.featureManager.scoreFeatures();

        if (shouldPlaceMeeple) {
            assertThat(this.player.getScore()).isEqualTo(MonasteryFeature.POINTS_PER_MONASTERY);
        } else {
            assertThat(this.player.getScore()).isEqualTo(0);
        }
    }

    @Test
    public void shouldNotScoreIncompleteMonastery() {
        Tile tile18 = tileManager.drawTileById(18);
        tile18.rotateClockwise();
        placeTileAndUpdate(tile18, 1, 0);

        TileSection monasterySection = tile18.getCenterSections().get(0);
        placeMeeple(monasterySection);

        assertFeatureCount(1);
        assertFeature(TileSectionType.MONASTERY, 1, false);

        this.featureManager.scoreFeatures();

        assertThat(this.player.getScore()).isEqualTo(0);
    }

    @Test
    public void shouldOnlyScoreCompletedMonasteryOnce() {
        shouldCompleteMonastery_completedByMonasteryTile(true);

        this.featureManager.scoreFeatures();
        assertThat(this.player.getScore()).isEqualTo(MonasteryFeature.POINTS_PER_MONASTERY);
    }

    @Test
    public void shouldConvertTileCoordinatesToIndices() {

        ImmutableMap<Pair<Integer>, Pair<Integer>> offsetXYToIndicesIJ = new ImmutableMap.Builder<Pair<Integer>, Pair<Integer>>()
                .put(new Pair<>(-1, 1), new Pair<>(0, 0))
                .put(new Pair<>(0, 1), new Pair<>(0, 1))
                .put(new Pair<>(1, 1), new Pair<>(0, 2))
                .put(new Pair<>(-1, 0), new Pair<>(1, 0))
                .put(new Pair<>(0, 0), new Pair<>(1, 1))
                .put(new Pair<>(1, 0), new Pair<>(1, 2))
                .put(new Pair<>(-1, -1), new Pair<>(2, 0))
                .put(new Pair<>(0, -1), new Pair<>(2, 1))
                .put(new Pair<>(1, -1), new Pair<>(2, 2))
                .build();

        for (Map.Entry<Pair<Integer>, Pair<Integer>> entry : offsetXYToIndicesIJ.entrySet()) {
            Pair<Integer> tileOffsetFromMonasteryXY = entry.getKey();
            Pair<Integer> expectedTileIndicesIJ = entry.getValue();
            int xOffset = new Random().nextInt();
            int yOffset = new Random().nextInt();

            int monasteryX = xOffset;
            int monasteryY = yOffset;
            int tileX = xOffset + tileOffsetFromMonasteryXY.getFirst();
            int tileY = yOffset + tileOffsetFromMonasteryXY.getSecond();

            Pair<Integer> indices = MonasteryFeatureManager.convertPositionsToIndices(new Pair<>(monasteryX, monasteryY),
                    new Pair<>(tileX, tileY));

            assertThat(indices.getFirst()).isEqualTo(expectedTileIndicesIJ.getFirst());
            assertThat(indices.getSecond()).isEqualTo(expectedTileIndicesIJ.getSecond());
        }
    }

    private void placeMeeple(TileSection tileSection) {
        Optional<Meeple> meeple = this.player.getMeeple();
        assertThat(meeple).isPresent();
        tileSection.placeMeeple(meeple.get());
    }

    private void assertFeatureCount(int count) {
        assertThat(this.featureManager.getFeatures()).hasSize(count);
    }

    private void assertFeature(TileSectionType type, int count, boolean isComplete) {
        Collection<? extends Feature> features = this.featureManager.getFeatures();

        assertThat(features).areExactly(count, new Condition<>(
                feature -> feature.getType() == type
                        && feature.isComplete() == isComplete,
                ""));
    }

    private void placeTileAndUpdate(Tile tile, int xPosition, int yPosition) {
        assertThat(PlacementValidator.isValid(this.board, xPosition, yPosition, tile)).isTrue();
        this.board.placeTile(tile, xPosition, yPosition);
        this.featureManager.updateFeatures(tile, xPosition, yPosition);
    }
}