package qrcodegenerator.models.enums;

import java.util.List;

public enum Direction {
    TOP_RIGHT,
    BOTTOM_RIGHT,
    LEFT;

    /**
     * @return Directions that may be followed after the evaluated one.
     */
    public List<Direction> getNextDirectionOptions() {
        return switch (this) {
            case TOP_RIGHT -> List.of(LEFT);
            case BOTTOM_RIGHT -> List.of(TOP_RIGHT);
            case LEFT -> List.of(TOP_RIGHT, LEFT, BOTTOM_RIGHT);
        };
    }
}
