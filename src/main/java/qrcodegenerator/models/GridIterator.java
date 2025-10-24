package qrcodegenerator.models;

import qrcodegenerator.models.enums.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    private boolean isGoingUp;

    public GridIterator(int upperBound) {
        this.upperBound = upperBound;
        this.currentPosition = new Position(upperBound, upperBound);
        this.currentDirection = Direction.LEFT;
        this.isGoingUp = true;
        // Defines boundaries, where and behind which data cannot be placed
        this.unskippableBoundaries = IntStream.range(0, 9)
                .mapToObj(x -> IntStream.range(0, 9)
                        .mapToObj(y -> Stream.of(
                                new Position(x, y),
                                new Position(upperBound - x, y),
                                new Position(x, upperBound - y)
                        ))
                )
                .flatMap(stream -> stream)
                .flatMap(stream -> stream)
                .collect(Collectors.toSet());
        IntStream.range(0, 9)
                .mapToObj(i -> Set.of(
                        new Position(i, upperBound - 8),
                        new Position(upperBound - 8, i)
                )).forEach(this.unskippableBoundaries::removeAll);
        this.skippableBoundaries = IntStream.range(0, 5)
                .mapToObj(x -> IntStream.range(0, 5)
                        .mapToObj(y -> new Position(upperBound - 8 + x, upperBound - 8 + y))
                )
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
    public List<Position> nextPositions() {
        // Computes the next position and direction
        Position nextPosition = this.currentPosition.move(this.currentDirection);
        Direction nextDirection = this.currentDirection.getNextDirection(this.isGoingUp);
        Optional<BoundaryType> boundaryTypeOpt = isBoundary(nextPosition);

        // Repeats until no unskippable boundary was found
        if (BoundaryType.SKIPPABLE == boundaryTypeOpt.orElse(BoundaryType.SKIPPABLE)) {
            while (Optional.of(BoundaryType.SKIPPABLE).equals(isBoundary(nextPosition))) {
                nextPosition = nextPosition.move(nextDirection);
                nextDirection = nextDirection.getNextDirection(this.isGoingUp);
            }
            this.currentPosition = nextPosition;
            this.currentDirection = nextDirection;
            return Collections.singletonList(this.currentPosition.copyOf());
        }

        // If the met boundary is unskippable, compute positions until it goes back to classic pattern
        List<Position> nextPositions = new ArrayList<>();
        // TODO JEV : implements the movement logic

        return nextPositions;
    }

    /**
     * @param position Position to check its boundary nature.
     * @return The potential boundary type of the position.
     */
    private Optional<BoundaryType> isBoundary(Position position) {
        if (this.skippableBoundaries.contains(position)) {
            return Optional.of(BoundaryType.SKIPPABLE);
        }

        if (this.unskippableBoundaries.contains(position)
                || position.x() < 0 && position.x() > upperBound
                || position.y() < 0 && position.y() > upperBound
        ) {
            return Optional.of(BoundaryType.UNSKIPPABLE);
        }

        return Optional.empty();
    }
}
