package net.basilcam.core.features;

import net.basilcam.core.*;
import net.basilcam.core.tiles.*;
import net.basilcam.gui.PlayerColor;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class GraphFeatureManagerTest {
    private PlayerManager playerManager;
    private TestTileManager tileManager;
    private Board board;
    private GraphFeatureManager featureManager;
    private Player player1;
    private Player player2;

    @BeforeEach
    public void beforeEach() {
        this.playerManager = new PlayerManager();
        this.tileManager = new TestTileManager();
        this.board = new Board(this.tileManager.getStartTile());
        this.featureManager = new GraphFeatureManager(this.playerManager, this.tileManager.getTileManager(), this.board);

        this.player1 = this.playerManager.addPlayer("cam", PlayerColor.RED);
        this.player2 = this.playerManager.addPlayer("basil", PlayerColor.YELLOW);
    }

    @Test
    public void shouldUpdateFeaturesForStartTile() {
        assertFeatureCount(2);
        assertFeature(TileSectionType.CITY, 1, false);
        assertFeature(TileSectionType.ROAD, 1, false);
    }

    @Test
    public void shouldUpdateFeatures_tilePlacedAboveStartTile() {
        Tile tile = tileManager.drawTileById(20);
        tile.rotateClockwise();
        tile.rotateClockwise();

        placeTileAndUpdate(tile, 0, 1);
        assertFeatureCount(2);
        assertFeature(TileSectionType.CITY, 1, true);
        assertFeature(TileSectionType.ROAD, 1, false);
    }

    @Test
    public void shouldUpdateFeatures_tilePlacedBesideTwoTiles() {
        Tile tile15 = tileManager.drawTileById(15);
        placeTileAndUpdate(tile15, 1, 0);
        assertFeatureCount(2);
        assertFeature(TileSectionType.CITY, 1, false);
        assertFeature(TileSectionType.ROAD, 1, false);

        Tile tile11 = tileManager.drawTileById(11);
        tile11.rotateClockwise();
        tile11.rotateClockwise();
        placeTileAndUpdate(tile11, 0, 1);
        assertFeatureCount(2);
        assertFeature(TileSectionType.CITY, 1, false);
        assertFeature(TileSectionType.ROAD, 1, false);

        Tile tile20 = tileManager.drawTileById(20);
        tile20.rotateClockwise();
        tile20.rotateClockwise();
        tile20.rotateClockwise();
        placeTileAndUpdate(tile20, 1, 1);
        assertFeatureCount(2);
        assertFeature(TileSectionType.CITY, 1, true);
        assertFeature(TileSectionType.ROAD, 1, false);
    }

    @Test
    public void shouldUpdateFeatures_splitCircleRoad() {
        Tile tile15 = tileManager.drawTileById(15);
        placeTileAndUpdate(tile15, 1, 0);
        assertFeatureCount(2);
        assertFeature(TileSectionType.CITY, 1, false);
        assertFeature(TileSectionType.ROAD, 1, false);

        Tile tile2 = tileManager.drawTileById(2);
        placeTileAndUpdate(tile2, 1, -1);
        assertFeatureCount(5);
        assertFeature(TileSectionType.CITY, 1, false);
        assertFeature(TileSectionType.ROAD, 4, false);

        Tile tile10 = tileManager.drawTileById(10);
        tile10.rotateClockwise();
        placeTileAndUpdate(tile10, 0, -1);
        assertFeatureCount(5);
        assertFeature(TileSectionType.CITY, 1, false);
        assertFeature(TileSectionType.ROAD, 4, false);

        Tile tile7 = tileManager.drawTileById(7);
        tile7.rotateClockwise();
        tile7.rotateClockwise();
        placeTileAndUpdate(tile7, -1, -1);
        assertFeatureCount(7);
        assertFeature(TileSectionType.CITY, 1, false);
        assertFeature(TileSectionType.ROAD, 5, false);
        assertFeature(TileSectionType.ROAD, 1, true);

        Tile tile14 = tileManager.drawTileById(14);
        placeTileAndUpdate(tile14, -1, 0);
        assertFeatureCount(7);
        assertFeature(TileSectionType.CITY, 2, false);
        assertFeature(TileSectionType.ROAD, 3, false);
        assertFeature(TileSectionType.ROAD, 2, true);
    }

    @Test
    public void shouldUpdateFeatures_continuousCircleRoad() {
        Tile tile15 = tileManager.drawTileById(15);
        placeTileAndUpdate(tile15, 1, 0);
        assertFeatureCount(2);
        assertFeature(TileSectionType.CITY, 1, false);
        assertFeature(TileSectionType.ROAD, 1, false);

        Tile tile15_2 = tileManager.drawTileById(15);
        tile15_2.rotateClockwise();
        placeTileAndUpdate(tile15_2, 1, -1);
        assertFeatureCount(2);
        assertFeature(TileSectionType.CITY, 1, false);
        assertFeature(TileSectionType.ROAD, 1, false);

        Tile tile10 = tileManager.drawTileById(10);
        tile10.rotateClockwise();
        placeTileAndUpdate(tile10, 0, -1);
        assertFeatureCount(2);
        assertFeature(TileSectionType.CITY, 1, false);
        assertFeature(TileSectionType.ROAD, 1, false);

        Tile tile15_3 = tileManager.drawTileById(15);
        tile15_3.rotateClockwise();
        tile15_3.rotateClockwise();
        placeTileAndUpdate(tile15_3, -1, -1);
        assertFeatureCount(2);
        assertFeature(TileSectionType.CITY, 1, false);
        assertFeature(TileSectionType.ROAD, 1, false);

        Tile tile14 = tileManager.drawTileById(14);
        placeTileAndUpdate(tile14, -1, 0);
        assertFeatureCount(3);
        assertFeature(TileSectionType.CITY, 2, false);
        assertFeature(TileSectionType.ROAD, 1, true);
    }

    @Test
    public void shouldNotAllowPlacingMeepleOnRoadWithExistingMeeple() {
        Tile tile10 = tileManager.drawTileById(10);
        tile10.rotateClockwise();
        placeTileAndUpdate(tile10, 1, 0);

        TileSection roadSection = tile10.getLeftSection();
        assertThat(this.featureManager.canPlaceMeeple(tile10, roadSection)).isTrue();
        placeMeeple(tile10, roadSection, this.player1);

        Tile tile15 = tileManager.drawTileById(15);
        placeTileAndUpdate(tile15, 2, 0);

        TileSection anotherRoadSection = tile15.getBottomSection();
        assertThat(this.featureManager.canPlaceMeeple(tile15, anotherRoadSection)).isFalse();
    }

    @Test
    public void shouldNotAllowPlacingMeepleOnCityWithExistingMeeple() {
        Tile tile8 = tileManager.drawTileById(8);
        tile8.rotateClockwise();
        placeTileAndUpdate(tile8, 0, 1);

        TileSection citySection = tile8.getBottomSection();
        assertThat(citySection.getType()).isEqualTo(TileSectionType.CITY);
        assertThat(this.featureManager.canPlaceMeeple(tile8, citySection)).isTrue();
        placeMeeple(tile8, citySection, this.player1);

        Tile tile20 = tileManager.drawTileById(20);
        tile20.rotateClockwise();
        tile20.rotateClockwise();
        placeTileAndUpdate(tile20, 0, 2);

        citySection = tile20.getBottomSection();
        assertThat(citySection.getType()).isEqualTo(TileSectionType.CITY);
        assertThat(this.featureManager.canPlaceMeeple(tile20, citySection)).isFalse();
    }

    @Test
    public void shouldScoreCity_singleMeeple() {
        // temp tiles are placed to make important tiles legal

        Tile tileTemp1 = tileManager.drawTileById(15);
        tileTemp1.rotateClockwise();
        tileTemp1.rotateClockwise();
        tileTemp1.rotateClockwise();
        placeTileAndUpdate(tileTemp1, -1, 0);

        Tile tileTemp2 = tileManager.drawTileById(15);
        placeTileAndUpdate(tileTemp2, 1, 0);

        Tile tile20_1 = tileManager.drawTileById(20);
        tile20_1.rotateClockwise();
        placeTileAndUpdate(tile20_1, -1, 1);
        placeMeeple(tile20_1, tile20_1.getRightSection(), this.player1);

        Tile tile20_2 = tileManager.drawTileById(20);
        tile20_2.rotateClockwise();
        tile20_2.rotateClockwise();
        tile20_2.rotateClockwise();
        placeTileAndUpdate(tile20_2, 1, 1);

        Tile tileTemp3 = tileManager.drawTileById(15);
        tileTemp3.rotateClockwise();
        tileTemp3.rotateClockwise();
        placeTileAndUpdate(tileTemp3, 1, 2);

        Tile tile20_3 = tileManager.drawTileById(20);
        tile20_3.rotateClockwise();
        tile20_3.rotateClockwise();
        placeTileAndUpdate(tile20_3, 0, 2);

        Tile tile1 = tileManager.drawTileById(1);
        placeTileAndUpdate(tile1, 0, 1);

        assertFeature(TileSectionType.CITY, 1, true);

        this.featureManager.scoreFeatures();

        int numberOfTiles = 5;
        int totalScore = GraphFeature.CITY_POINTS_PER_TILE * numberOfTiles + GraphFeature.COAT_OF_ARMS_POINTS_PER_TILE;
        assertThat(this.player1.getScore()).isEqualTo(totalScore);
        assertThat(this.player2.getScore()).isEqualTo(0);
    }

    @Test
    public void shouldScoreCity_twoDifferentPlayerMeeples_sameNumber() {
        // temp tiles are placed to make important tiles legal

        Tile tileTemp1 = tileManager.drawTileById(15);
        tileTemp1.rotateClockwise();
        tileTemp1.rotateClockwise();
        tileTemp1.rotateClockwise();
        placeTileAndUpdate(tileTemp1, -1, 0);

        Tile tileTemp2 = tileManager.drawTileById(15);
        placeTileAndUpdate(tileTemp2, 1, 0);

        Tile tile20_1 = tileManager.drawTileById(20);
        tile20_1.rotateClockwise();
        placeTileAndUpdate(tile20_1, -1, 1);
        placeMeeple(tile20_1, tile20_1.getRightSection(), this.player1);

        Tile tile20_2 = tileManager.drawTileById(20);
        tile20_2.rotateClockwise();
        tile20_2.rotateClockwise();
        tile20_2.rotateClockwise();
        placeTileAndUpdate(tile20_2, 1, 1);
        placeMeeple(tile20_2, tile20_2.getLeftSection(), this.player2);

        Tile tileTemp3 = tileManager.drawTileById(15);
        tileTemp3.rotateClockwise();
        tileTemp3.rotateClockwise();
        placeTileAndUpdate(tileTemp3, 1, 2);

        Tile tile20_3 = tileManager.drawTileById(20);
        tile20_3.rotateClockwise();
        tile20_3.rotateClockwise();
        placeTileAndUpdate(tile20_3, 0, 2);

        Tile tile1 = tileManager.drawTileById(1);
        placeTileAndUpdate(tile1, 0, 1);

        assertFeature(TileSectionType.CITY, 1, true);

        this.featureManager.scoreFeatures();

        int numberOfTiles = 5;
        int totalScore = GraphFeature.CITY_POINTS_PER_TILE * numberOfTiles + GraphFeature.COAT_OF_ARMS_POINTS_PER_TILE;
        int pointsPerPlayer = totalScore / 2;
        assertThat(this.player1.getScore()).isEqualTo(pointsPerPlayer);
        assertThat(this.player2.getScore()).isEqualTo(pointsPerPlayer);
    }

    @Test
    public void shouldScoreCity_twoDifferentPlayerMeeples_oneHasMore() {
        // temp tiles are placed to make important tiles legal

        Tile tileTemp1 = tileManager.drawTileById(15);
        tileTemp1.rotateClockwise();
        tileTemp1.rotateClockwise();
        tileTemp1.rotateClockwise();
        placeTileAndUpdate(tileTemp1, -1, 0);

        Tile tileTemp2 = tileManager.drawTileById(15);
        placeTileAndUpdate(tileTemp2, 1, 0);

        Tile tile20_1 = tileManager.drawTileById(20);
        tile20_1.rotateClockwise();
        placeTileAndUpdate(tile20_1, -1, 1);
        placeMeeple(tile20_1, tile20_1.getRightSection(), this.player1);

        Tile tile20_2 = tileManager.drawTileById(20);
        tile20_2.rotateClockwise();
        tile20_2.rotateClockwise();
        tile20_2.rotateClockwise();
        placeTileAndUpdate(tile20_2, 1, 1);
        placeMeeple(tile20_2, tile20_2.getLeftSection(), this.player2);

        Tile tileTemp3 = tileManager.drawTileById(15);
        tileTemp3.rotateClockwise();
        tileTemp3.rotateClockwise();
        placeTileAndUpdate(tileTemp3, 1, 2);

        Tile tile20_3 = tileManager.drawTileById(20);
        tile20_3.rotateClockwise();
        tile20_3.rotateClockwise();
        placeTileAndUpdate(tile20_3, 0, 2);
        placeMeeple(tile20_3, tile20_3.getBottomSection(), this.player1);

        Tile tile1 = tileManager.drawTileById(1);
        placeTileAndUpdate(tile1, 0, 1);

        assertFeature(TileSectionType.CITY, 1, true);

        this.featureManager.scoreFeatures();

        int numberOfTiles = 5;
        int totalScore = GraphFeature.CITY_POINTS_PER_TILE * numberOfTiles + GraphFeature.COAT_OF_ARMS_POINTS_PER_TILE;
        assertThat(this.player1.getScore()).isEqualTo(totalScore);
        assertThat(this.player2.getScore()).isEqualTo(0);
    }

    @Test
    public void shouldScoreCity_twoSamePlayerMeeples() {
        // temp tiles are placed to make important tiles legal

        Tile tileTemp1 = tileManager.drawTileById(15);
        tileTemp1.rotateClockwise();
        tileTemp1.rotateClockwise();
        tileTemp1.rotateClockwise();
        placeTileAndUpdate(tileTemp1, -1, 0);

        Tile tileTemp2 = tileManager.drawTileById(15);
        placeTileAndUpdate(tileTemp2, 1, 0);

        Tile tile20_1 = tileManager.drawTileById(20);
        tile20_1.rotateClockwise();
        placeTileAndUpdate(tile20_1, -1, 1);
        placeMeeple(tile20_1, tile20_1.getRightSection(), this.player1);

        Tile tile20_2 = tileManager.drawTileById(20);
        tile20_2.rotateClockwise();
        tile20_2.rotateClockwise();
        tile20_2.rotateClockwise();
        placeTileAndUpdate(tile20_2, 1, 1);
        placeMeeple(tile20_2, tile20_2.getLeftSection(), this.player1);

        Tile tileTemp3 = tileManager.drawTileById(15);
        tileTemp3.rotateClockwise();
        tileTemp3.rotateClockwise();
        placeTileAndUpdate(tileTemp3, 1, 2);

        Tile tile20_3 = tileManager.drawTileById(20);
        tile20_3.rotateClockwise();
        tile20_3.rotateClockwise();
        placeTileAndUpdate(tile20_3, 0, 2);

        Tile tile1 = tileManager.drawTileById(1);
        placeTileAndUpdate(tile1, 0, 1);

        assertFeature(TileSectionType.CITY, 1, true);

        this.featureManager.scoreFeatures();

        int numberOfTiles = 5;
        int totalScore = GraphFeature.CITY_POINTS_PER_TILE * numberOfTiles + GraphFeature.COAT_OF_ARMS_POINTS_PER_TILE;
        assertThat(this.player1.getScore()).isEqualTo(totalScore);
        assertThat(this.player2.getScore()).isEqualTo(0);
    }

    private void placeMeeple(Tile tile, TileSection tileSection, Player player) {
        Optional<Meeple> meeple = player.getMeeple();
        assertThat(meeple).isPresent();

        assertThat(this.featureManager.canPlaceMeeple(tile, tileSection)).isTrue();

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
