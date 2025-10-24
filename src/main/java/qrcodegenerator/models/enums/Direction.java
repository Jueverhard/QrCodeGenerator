package qrcodegenerator.models.enums;

public enum Direction {
    TOP_RIGHT,
    BOTTOM_RIGHT,
    LEFT;

    /**
     * @param isGoingUp Whether the current route is going up.
     * @return The next direction that should be followed.
     */
    public Direction getNextDirection(boolean isGoingUp) {
        return switch (this) {
            case TOP_RIGHT -> LEFT;
            case BOTTOM_RIGHT -> TOP_RIGHT;
            case LEFT -> isGoingUp ? TOP_RIGHT : BOTTOM_RIGHT;
        };
    }
}
