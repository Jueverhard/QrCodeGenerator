package qrcodegenerator.models;

import qrcodegenerator.models.enums.Direction;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GridIterator {

    private final int upperBound;

    private final Set<Position> unskippableBoundaries;

    private final Set<Position> skippableBoundaries;

    private Position currentPosition;

    private Direction currentDirection;

    public GridIterator(int upperBound, Direction startDirection) {
        this.upperBound = upperBound;
        this.currentPosition = new Position(upperBound, upperBound);
        this.currentDirection = startDirection;
        // Defines boundaries, where and behind which data cannot be placed
        this.unskippableBoundaries = IntStream.range(0, 9)
                .mapToObj(i -> Stream.of(
                        new Position(i, 8),
                        new Position(upperBound - i, 8),
                        new Position(i, upperBound - 7),
                        new Position(8, i),
                        new Position(8, upperBound - i),
                        new Position(upperBound - 7, i)
                ))
                .flatMap(stream -> stream)
                .collect(Collectors.toSet());
        this.unskippableBoundaries.remove(new Position(8, upperBound - 8));
        this.unskippableBoundaries.remove(new Position(upperBound - 8, 8));
        this.skippableBoundaries = IntStream.range(0, 5)
                .mapToObj(i -> Stream.of(
                        new Position(upperBound - 4 - i, upperBound - 4),
                        new Position(upperBound - 4, upperBound - 4 - i),
                        new Position(upperBound - 4 - i, upperBound - 8),
                        new Position(upperBound - 8, upperBound - 4 - i)
                ))
                .flatMap(stream -> stream)
                .collect(Collectors.toSet());
        this.skippableBoundaries.addAll(IntStream.range(9, upperBound - 7)
                .mapToObj(i -> Stream.of(
                        new Position(6, i),
                        new Position(i, 6)
                ))
                .flatMap(stream -> stream)
                .collect(Collectors.toSet())
        );
    }

    /**
     * @return Whether there is a next position to visit.
     */
    public boolean hasNext() {
        return !(0 == currentPosition.x() && upperBound - 7 == currentPosition.y());
    }

    /**
     * Moves to the next position according to the grid movement logic.
     */
    public void next() {
        // TODO JEV : implements the movement logic
        Position nextPosition = currentPosition.move(currentDirection);
        if (isLegit(nextPosition)) {
            this.currentPosition = nextPosition;
            this.currentDirection = this.currentDirection.getNextDirectionOptions().getFirst();
        }
    }

    /**
     * @param position Position to check legitimacy for.
     * @return Whether the position is legitimate to encode data to.
     */
    private boolean isLegit(Position position) {
        return !this.skippableBoundaries.contains(position)
                && !this.unskippableBoundaries.contains(position)
                && position.x() >= 0 && position.x() <= upperBound
                && position.y() >= 0 && position.y() <= upperBound;
    }

    /**
     * @return The current position of the iterator.
     */
    public Position getCurrentPosition() {
        return currentPosition.copyOf();
    }
}
