package qrcodegenerator.models;

import qrcodegenerator.models.enums.EncodingMode;
import qrcodegenerator.models.enums.Version;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.NoSuchElementException;
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
        this.pixels.addAll(this.generateMetadataPixels(positionsToFill, encodingMode, chainToRepresent));

        // Encodes the data into pixels
        byte[] dataBytes = chainToRepresent.getBytes();
        for (byte dataByte : dataBytes) {
            for (int i = 7; i >= 0; i--) {
                try {
                    Position position = positionsToFill.remove();
                    boolean isBitSet = ((dataByte >> i) & 1) == 1;
                    if (isBitSet) {
                        this.pixels.add(position);
                    }
                } catch (NoSuchElementException e) {
                    // Shouldn't happen due to version selection. However, due to the current grid iteration, it may
                    log.severe("Not enough space to encode the data in the QR Code!");
                    return;
                }
            }
        }
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
        int upperBound = this.version.getCodeWidth() - 1;
        Queue<Position> positionsToFill = new ArrayDeque<>();
        GridIterator gridIterator = new GridIterator(upperBound);
        positionsToFill.add(new Position(upperBound, upperBound));

        while (gridIterator.hasNext() && positionsToFill.size() < 30) {
            positionsToFill.addAll(gridIterator.nextPositions());
        }

        return positionsToFill;
    }

    /**
     * Generates metadata pixels (encoding mode and data length).
     *
     * @param positionsToFill Positions to fill.
     * @param encodingMode    Encoding mode used.
     * @param data            Data to encode.
     * @return The set of filled pixels.
     */
    private Set<Position> generateMetadataPixels(Queue<Position> positionsToFill, EncodingMode encodingMode, String data) {
        // Fills encoding mode metadata
        Set<Position> metadataPixels = new HashSet<>();
        for (int i = 3; i >= 0; i--) {
            Position position = positionsToFill.remove();
            if (i == encodingMode.ordinal()) {
                metadataPixels.add(position);
            }
        }

        // Fills data length metadata
        byte dataLength = convertToByte(data.length());
        for (int i = 7; i >= 0; i--) {
            Position position = positionsToFill.remove();
            boolean isBitSet = ((dataLength >> i) & 1) == 1;
            if (isBitSet) {
                metadataPixels.add(position);
            }
        }

        return metadataPixels;
    }

    /**
     * Converts an integer value to a byte, keeping only the 8 least significant bits.
     *
     * @param value Integer value to convert.
     * @return The resulting bits as a byte.
     */
    private byte convertToByte(int value) {
        return (byte) (value & 0xFF);
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
