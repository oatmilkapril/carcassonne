package net.basilcam.core.features;

import net.basilcam.core.Direction;
import net.basilcam.core.tiles.TileSection;

public class GraphFeatureNode {
    public static final GraphFeatureNode OPEN_NODE = new GraphFeatureNode(null);
    public static final GraphFeatureNode CLOSED_NODE = new GraphFeatureNode(null);

    private static final String EXISTING_EDGE_ERROR = "another node is already connected to this side of the given node";
    private TileSection tileSection;
    private GraphFeatureNode topNode;
    private GraphFeatureNode leftNode;
    private GraphFeatureNode bottomNode;
    private GraphFeatureNode rightNode;

    // if a given node is empty, another node can be connected to it
    // if a given node is a terminator, no node can be connected to it

    public GraphFeatureNode(TileSection tileSection) {
        this.tileSection = tileSection;
        this.topNode = OPEN_NODE;
        this.leftNode = OPEN_NODE;
        this.bottomNode = OPEN_NODE;
        this.rightNode = OPEN_NODE;
    }

    public TileSection getTileSection() {
        return this.tileSection;
    }

    public GraphFeatureNode getNode(Direction direction) {
        switch (direction) {
            case UP:
                return this.topNode;
            case LEFT:
                return this.leftNode;
            case DOWN:
                return this.bottomNode;
            case RIGHT:
                return this.rightNode;
            default:
                throw new RuntimeException();
        }
    }

    public boolean hasOpenConnection() {
        return this.topNode == OPEN_NODE
                || this.leftNode == OPEN_NODE
                || this.bottomNode == OPEN_NODE
                || this.rightNode == OPEN_NODE;
    }

    public void connectNode(GraphFeatureNode node, Direction directionFromExisting) {
        switch (directionFromExisting) {
            case UP:
                setTopNode(node);
                break;
            case LEFT:
                setLeftNode(node);
                break;
            case DOWN:
                setBottomNode(node);
                break;
            case RIGHT:
                setRightNode(node);
                break;
        }
    }

    public void closeNode(Direction direction) {
        connectNode(CLOSED_NODE, direction);
    }

    private void setTopNode(GraphFeatureNode node) {
//        if (this.topNode != OPEN_NODE) {
//            throw new IllegalStateException(EXISTING_EDGE_ERROR);
//        }
        this.topNode = node;
    }

    private void setLeftNode(GraphFeatureNode node) {
//        if (this.leftNode != OPEN_NODE) {
//            throw new IllegalStateException(EXISTING_EDGE_ERROR);
//        }
        this.leftNode = node;
    }

    private void setBottomNode(GraphFeatureNode node) {
//        if (this.bottomNode != OPEN_NODE) {
//            throw new IllegalStateException(EXISTING_EDGE_ERROR);
//        }
        this.bottomNode = node;
    }

    private void setRightNode(GraphFeatureNode node) {
//        if (this.rightNode != OPEN_NODE) {
//            throw new IllegalStateException(EXISTING_EDGE_ERROR);
//        }
        this.rightNode = node;
    }

}
