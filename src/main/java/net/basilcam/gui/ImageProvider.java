package net.basilcam.gui;

import net.basilcam.core.Player;
import net.basilcam.core.tiles.Tile;
import net.basilcam.core.tiles.TileSectionLocation;
import net.basilcam.core.tiles.TileStackFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageProvider {
    public static final int MEEPLE_RADIUS = 10;
    public static final int MEEPLE_SIZE = 50;
    public static final int TILE_SIZE = 100;
    private final Map<Integer, BufferedImage> idToTileImage;
    private final Map<PlayerColor, BufferedImage> colorToMeepleImage;

    public ImageProvider() {
        this.idToTileImage = new HashMap<>();
        this.colorToMeepleImage = new HashMap<>();
        loadTileImages();
        loadMeepleImages();
    }

    public BufferedImage getTileImage(Tile tile) {
        BufferedImage image = this.idToTileImage.get(tile.getId());
        return rotateImage(image, tile.getClockWiseRotationCount());
    }

    public BufferedImage getTileImageWithMeeple(Tile tile, TileSectionLocation tileSectionLocation, PlayerColor color) {
        BufferedImage image = getTileImage(tile);
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        int[] myLocation = convertSectionLocationToPixelLocation(tileSectionLocation);

        Graphics2D grapgics = (Graphics2D) newImage.getGraphics();
        grapgics.drawImage(image, 0, 0, null);
        grapgics.setColor(convertPlayerColorToGraphicsColor(color));
        grapgics.fillOval(myLocation[0] - MEEPLE_RADIUS, myLocation[1] - MEEPLE_RADIUS, 2 * MEEPLE_RADIUS, 2 * MEEPLE_RADIUS);
        grapgics.dispose();

        return newImage;
    }

    private static Color convertPlayerColorToGraphicsColor(PlayerColor color) {
        switch (color) {
            case RED:
                return Color.RED;
            case YELLOW:
                return Color.YELLOW;
            case GREEN:
                return Color.GREEN;
            case BLUE:
                return Color.BLUE;
            case BLACK:
                return Color.BLACK;
            default:
                throw new RuntimeException("invalid color");
        }
    }

    private static int[] convertSectionLocationToPixelLocation(TileSectionLocation location) {
        switch (location) {
            case TOP:
                return new int[]{TILE_SIZE / 2, 0};
            case LEFT:
                return new int[]{0, TILE_SIZE / 2};
            case BOTTOM:
                return new int[]{TILE_SIZE / 2, TILE_SIZE};
            case RIGHT:
                return new int[]{TILE_SIZE, TILE_SIZE / 2};
            case CENTER:
                return new int[]{TILE_SIZE / 2, TILE_SIZE / 2};
            default:
                throw new RuntimeException("invalid location");
        }
    }

    public BufferedImage getMeepleImage(PlayerColor color) {
        return this.colorToMeepleImage.get(color);
    }

    private void loadTileImages() {
        try {
            for (int id = 1; id <= TileStackFactory.MAX_TILE_ID; id++) {
                URL url = ImageProvider.class.getResource("/tiles/" + id + ".png");
                assert url != null;
                BufferedImage image = ImageIO.read(url);
                this.idToTileImage.put(id, scaleImage(image, TILE_SIZE, TILE_SIZE));
            }
        } catch (IOException e) {
            throw new RuntimeException("missing tile");
        }
    }

    private void loadMeepleImages() {
        try {
            for (PlayerColor color : PlayerColor.values()) {
                URL stream = getClass().getResource("/" + color.name().toLowerCase() + "_meeple.png");
                assert stream != null;
                BufferedImage image = ImageIO.read(stream);
                this.colorToMeepleImage.put(color, scaleImage(image, MEEPLE_SIZE, MEEPLE_SIZE));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static BufferedImage scaleImage(BufferedImage image, int newWidth, int newHeight) {
        double xScale = newWidth / (double) image.getWidth();
        double yScale = newHeight / (double) image.getHeight();

       BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        AffineTransform transform = new AffineTransform();
        transform.scale(xScale, yScale);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(image, scaledImage);
    }

    private static BufferedImage rotateImage(BufferedImage image, int clockwiseRotationCount) {
        if (clockwiseRotationCount == 0) {
            return image;
        }

        int width = image.getWidth();
        int height = image.getHeight();
        double rotationRadians = (Math.PI / 2) * clockwiseRotationCount;

        BufferedImage rotatedImage = new BufferedImage(height, width, image.getType());

        Graphics2D graphics = rotatedImage.createGraphics();
        graphics.translate((height - width) / 2, (height - width) / 2);
        graphics.rotate(rotationRadians, height / 2.0, width / 2.0);
        graphics.drawRenderedImage(image, null);

        return rotatedImage;
    }
}
