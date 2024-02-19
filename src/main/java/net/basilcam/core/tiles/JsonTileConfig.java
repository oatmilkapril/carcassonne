package net.basilcam.core.tiles;

public class JsonTileConfig {
    public static final String TILES_FILE_NAME = "tiles.json";
    public static final String CITY_SECTION = "city";
    public static final String FIELD_SECTION = "field";
    public static final String MONASTERY_SECTION = "monastery";
    public static final String ROAD_SECTION = "road";
    public static final String NONE_SECTION = "none";

    static class JsonTile {
        int id;
        int quantity;
        String topSection;
        String leftSection;
        String bottomSection;
        String rightSection;
        String[] centerSections;
        boolean hasCoatOfArms;
    }

    static class JsonTileStack {
        JsonTile[] tiles;
    }

    public static TileSection convertTypeNameToTileSection(String typeName) {
        TileSectionType type = Enum.valueOf(TileSectionType.class, typeName.toUpperCase());
        return new TileSection(type);
    }
}
