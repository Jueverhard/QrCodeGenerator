package qrcodegenerator.models;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class QrCode {

    private static final Logger log = Logger.getLogger(QrCode.class.getName());

    private final Version version;

    private final Set<Position> blacks;

    public QrCode(String chainToRepresent) {
        this.version = Version.fromChainSize(chainToRepresent.length());
        // TODO JEV : defines every black positions according to the QR Code encoding specs
        this.blacks = new HashSet<>();
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
}
