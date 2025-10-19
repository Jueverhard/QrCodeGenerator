package qrcodegenerator.models;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QrCode {

    private static final Logger log = Logger.getLogger(QrCode.class.getName());

    private final Version version;

    private final Set<Position> blacks;

    public QrCode(String chainToRepresent) {
        this.version = Version.fromChainSize(chainToRepresent.length());
        // TODO JEV : defines every black positions according to the QR Code encoding specs
        this.blacks = this.generateCorners();
    }

    /**
     * Prints the QR Code on the standard output.
     */
    public void print() {
        log.info("Required version identified: " + this.version);
        StringBuilder builder = new StringBuilder("Here's the corresponding QR Code:\n");
        for (int y = 0; y < this.version.getCodeWidth(); y++) {
            for (int x = 0; x <= this.version.getCodeWidth(); x++) {
                int finalX = x;
                int finalY = y;
                boolean isBlack = this.blacks.stream()
                        .anyMatch(black -> finalX == black.x() && finalY == black.y());
                builder.append(isBlack ? '#' : '.');
            }
            builder.append("\n");
        }

        log.info(builder.toString());
    }

    /**
     * Generates the top-left, top-right and bottom-left positioning corners.
     * @return Every corner-filled positions.
     */
    private Set<Position> generateCorners() {
        // Generates full corners
        int upperBound = this.version.getCodeWidth();
        Set<Position> corners = IntStream.range(0, 7)
                .mapToObj(x -> IntStream.range(0, 7)
                        .mapToObj(y -> Set.of(
                                new Position(x, y),
                                new Position(x, upperBound - y),
                                new Position(upperBound - x, y)
                        )).flatMap(Set::stream)
                        .toList()
                )
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        // Removes empty fields
        corners.removeIf(pos -> Set.of(1, 5, upperBound - 5, upperBound - 1).contains(pos.x()) &&
                !Set.of(0, 6, upperBound - 6, upperBound).contains(pos.y())
        );
        corners.removeIf(pos -> Set.of(1, 5, upperBound - 5, upperBound - 1).contains(pos.y()) &&
                !Set.of(0, 6, upperBound - 6, upperBound).contains(pos.x())
        );

        return corners;
    }
}
