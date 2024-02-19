package net.basilcam.core;

public enum Direction {
    UP,
    LEFT,
    DOWN,
    RIGHT;

    public Direction oppositeDirection() {
        switch (this) {
            case UP:
                return DOWN;
            case LEFT:
                return RIGHT;
            case DOWN:
                return UP;
            case RIGHT:
                return LEFT;
            default:
                throw new IllegalArgumentException(); // todo
        }
    }

    public Direction[] perpendicularDirections() {
        switch (this) {
            case UP:
            case DOWN:
                return new Direction[] {LEFT, RIGHT};
            case LEFT:
            case RIGHT:
                return new Direction[] {UP, DOWN};
            default:
                throw new IllegalArgumentException(); // todo
        }
    }
}
