package net.basilcam.core.tiles;

import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Stack;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class TileStackFactoryTest {

    @Test
    public void deserializedTileStackShouldContain72Tiles() {
        Stack<Tile> tileStack = TileStackFactory.createTileStack();
        assertThat(tileStack).hasSize(72);
    }

    @Test
    public void deserializedStartTileShouldMatchSpec() {
        Multimap<Integer, Tile> tileMap = TileStackFactory.createTileMap();
        Collection<Tile> tiles = tileMap.get(24);

        assertThat(tiles).hasSize(4);
        for (Tile tile: tiles) {
            assertThat(tile.getTopSection().getType()).isEqualTo(TileSectionType.CITY);
            assertThat(tile.getLeftSection().getType()).isEqualTo(TileSectionType.ROAD);
            assertThat(tile.getBottomSection().getType()).isEqualTo(TileSectionType.FIELD);
            assertThat(tile.getRightSection().getType()).isEqualTo(TileSectionType.ROAD);
            assertThat(tile.getCenterSections().stream()
                    .map(TileSection::getType)
                    .collect(Collectors.toList()))
                    .containsExactly(TileSectionType.ROAD);
        }
    }

    @Test
    public void deserializedTilesShouldHaveIdsInRange() {
        Stack<Tile> tileStack = TileStackFactory.createTileStack();

        assertThat(tileStack.stream().map(Tile::getId).collect(Collectors.toList()))
                .allMatch(id -> id > 0 && id <= TileStackFactory.MAX_TILE_ID);
    }

}