package qrcodegenerator.models;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QrCode {

    private static final Logger log = Logger.getLogger(QrCode.class.getName());

    private final Version version;

    private final Set<Position> pixels;

    public QrCode(String chainToRepresent) {
        // Determines the best encoding mode and the minimal version required
        EncodingMode encodingMode = EncodingMode.selectBestMode(chainToRepresent);
        this.version = Version.fromChainSize(chainToRepresent.length(), encodingMode);

        // Generates every format pixels
        this.pixels = this.generateFormatPixels();

        // Defines every ordered position to fill with data
        final Queue<Position> positionsToFill = this.prepareEveryPositionsToBeFilled();

        // Encodes the metadata into pixels
        // TODO JEV : to implement

        // Encodes the data into pixels
        // TODO JEV : to implement
    }

    /**
     * Generates every format pixels: this includes the top-left, top-right and bottom-left corners, the smaller
     * bottom-right square, and the corners joining dots.
     *
     * @return Every format pixels.
     */
    private Set<Position> generateFormatPixels() {
        // Generates fully-filled corners
        int upperBound = this.version.getCodeWidth() - 1;
        Set<Position> fixedPixels = generatePositioningSquare(new Position(0, 0), 7);
        fixedPixels.addAll(generatePositioningSquare(new Position(0, upperBound - 6), 7));
        fixedPixels.addAll(generatePositioningSquare(new Position(upperBound - 6, 0), 7));

        // Generates smaller bottom-right square
        fixedPixels.addAll(generatePositioningSquare(
                new Position(upperBound - 8, upperBound - 8),
                5
        ));

        // Generates joining dots
        IntStream.range(8, upperBound - 6)
                .filter(i -> i % 2 == 0)
                .mapToObj(i -> Set.of(
                        new Position(6, i),
                        new Position(i, 6)
                ))
                .flatMap(Set::stream)
                .forEach(fixedPixels::add);

        // Generates fixed bottom-left dark pixel
        fixedPixels.add(new Position(8, upperBound - 7));

        return fixedPixels;
    }

    /**
     * Generates a positioning square of given size at given position.
     *
     * @param topLeft Top-left position of the square.
     * @param size    Size of the square.
     * @return The set of pixels composing the square.
     */
    private Set<Position> generatePositioningSquare(Position topLeft, int size) {
        // Generates a fully-filled square of given size at given position
        Set<Position> squarePixels = IntStream.range(topLeft.x(), topLeft.x() + size)
                .mapToObj(x -> IntStream.range(topLeft.y(), topLeft.y() + size)
                        .mapToObj(y -> new Position(x, y))
                )
                .flatMap(stream -> stream)
                .collect(Collectors.toSet());

        // Removes inner empty part
        squarePixels.removeIf(pos -> Set.of(topLeft.x() + 1, topLeft.x() + size - 2).contains(pos.x()) &&
                !Set.of(topLeft.y(), topLeft.y() + size - 1).contains(pos.y())
        );
        squarePixels.removeIf(pos -> Set.of(topLeft.y() + 1, topLeft.y() + size - 2).contains(pos.y()) &&
                !Set.of(topLeft.x(), topLeft.x() + size - 1).contains(pos.x())
        );

        return squarePixels;
    }

    /**
     * @return Every position to be filled, ordered.
     */
    private Queue<Position> prepareEveryPositionsToBeFilled() {
        Queue<Position> positionsToFill = new ArrayDeque<>();
        // TODO JEV : implements the generation of every position to be filled with data
        int upperBound = this.version.getCodeWidth() - 1;
        positionsToFill.addAll(List.of(
                new Position(upperBound, upperBound),
                new Position(upperBound - 1, upperBound),
                new Position(upperBound, upperBound - 1),
                new Position(upperBound - 1, upperBound - 1),
                new Position(upperBound, upperBound - 2),
                new Position(upperBound - 1, upperBound - 2),
                new Position(upperBound, upperBound - 3),
                new Position(upperBound - 1, upperBound - 3),
                new Position(upperBound, upperBound - 4),
                new Position(upperBound - 1, upperBound - 4),
                new Position(upperBound, upperBound - 5),
                new Position(upperBound - 1, upperBound - 5),
                new Position(upperBound, upperBound - 6),
                new Position(upperBound - 1, upperBound - 6),
                new Position(upperBound, upperBound - 7),
                new Position(upperBound - 1, upperBound - 7),
                new Position(upperBound, upperBound - 8),
                new Position(upperBound - 1, upperBound - 8),
                new Position(upperBound, upperBound - 9),
                new Position(upperBound - 1, upperBound - 9)
        ));
        return positionsToFill;
    }

    /**
     * Prints the QR Code on the standard output.
     */
    public void print() {
        log.info("Required version identified: " + this.version);
        StringBuilder builder = new StringBuilder("Here's the corresponding QR Code:\n");
        for (int y = 0; y < this.version.getCodeWidth(); y++) {
            for (int x = 0; x < this.version.getCodeWidth(); x++) {
                int finalX = x;
                int finalY = y;
                boolean isBlack = this.pixels.stream()
                        .anyMatch(black -> finalX == black.x() && finalY == black.y());
                builder.append(isBlack ? '#' : '.');
            }
            builder.append("\n");
        }

        log.info(builder.toString());
    }
}
