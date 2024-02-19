package net.basilcam.core;

import net.basilcam.core.tiles.TestTileManager;
import net.basilcam.core.tiles.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlacementValidatorTest {
    private TestTileManager tileManager;
    private Board board;

    @BeforeEach
    public void beforeEach() {
        this.tileManager = new TestTileManager();
        this.board = new Board(this.tileManager.getStartTile());
    }

    @Test
    public void invalidTilePlacement_placedOnExistingTile() {
        Tile tile = tileManager.drawTileById(24);

        assertThat(isValid(tile, 0, 0)).isFalse();
    }

    @Test
    public void invalidTilePlacement_mismatchingSections_placedAboveExistingTile() {
        Tile tile = tileManager.drawTileById(2);

        assertThat(isValid(tile, 0, 1)).isFalse();
    }

    @Test
    public void invalidTilePlacement_mismatchingSections_placedLeftOfExistingTile() {
        Tile tile = tileManager.drawTileById(22);

        assertThat(isValid(tile, -1, 0)).isFalse();
    }

    @Test
    public void invalidTilePlacement_mismatchingSections_placedBelowExistingTile() {
        Tile tile = tileManager.drawTileById(14);

        assertThat(isValid(tile, 0, -1)).isFalse();
    }

    @Test
    public void invalidTilePlacement_mismatchingSections_placedRightOfExistingTile() {
        Tile tile = tileManager.drawTileById(14);

        assertThat(isValid(tile, 1, 0)).isFalse();
    }

    @Test
    public void validTilePlacement_matchingSections_placedAboveExistingTile() {
        Tile tile = tileManager.drawTileById(1);

        assertThat(isValid(tile, 0, 1)).isTrue();
    }

    @Test
    public void validTilePlacement_matchingSections_placedLeftOfExistingTile() {
        Tile tile = tileManager.drawTileById(2);

        assertThat(isValid(tile, -1, 0)).isTrue();
    }

    @Test
    public void validTilePlacement_matchingSections_placedBelowExistingTile() {
        Tile tile = tileManager.drawTileById(15);

        assertThat(isValid(tile, 0, -1)).isTrue();
    }

    @Test
    public void validTilePlacement_matchingSections_placedRightOfExistingTile() {
        Tile tile = tileManager.drawTileById(22);

        assertThat(isValid(tile, 1, 0)).isTrue();
    }

    @Test
    public void invalidTilePlacement_oneMatchingSection_oneMismatchingSection() {
        Tile validTile = tileManager.drawTileById(7);
        assertThat(isValid(validTile, 1, 0)).isTrue();
        this.board.placeTile(validTile, 1, 0);

        Tile anotherValidTile = tileManager.drawTileById(1);
        assertThat(isValid(anotherValidTile, 0, 1)).isTrue();
        this.board.placeTile(validTile, 0, 1);

        // the abutting sections between anotherValidTile (city) and this (city) match
        // the abutting sections between validTile (field) and this (road) don't match
        Tile invalidTile = tileManager.drawTileById(13);
        assertThat(isValid(invalidTile, 1, 1)).isFalse();
    }

    @Test
    public void invalidTilePlacement_noAbuttingTiles() {
        Tile tile = tileManager.drawTileById(1);
        assertThat(isValid(tile, 0, 2)).isFalse();
    }

    private boolean isValid(Tile tile, int xPosition, int yPosition) {
        return PlacementValidator.isValid(this.board, xPosition, yPosition, tile);
    }
}