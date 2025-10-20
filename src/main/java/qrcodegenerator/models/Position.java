package qrcodegenerator.models;

import qrcodegenerator.models.enums.Direction;

public record Position(int x, int y) {

    public Position move(Direction direction) {
        return switch (direction) {
            case TOP_RIGHT -> new Position(this.x + 1, this.y - 1);
            case BOTTOM_RIGHT -> new Position(this.x + 1, this.y + 1);
            case LEFT -> new Position(this.x - 1, this.y);
        };
    }

    public Position copyOf() {
        return new Position(this.x, this.y);
    }
}
