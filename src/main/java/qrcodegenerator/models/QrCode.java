package qrcodegenerator.models;

import java.util.logging.Logger;

public class QrCode {

    private static final Logger log = Logger.getLogger(QrCode.class.getName());

    private final Version version;

    public QrCode(String chainToRepresent) {
        this.version = Version.fromChainSize(chainToRepresent.length());
    }

    /**
     * Prints the QR Code on the standard output.
     */
    public void print() {
        log.info("Required version identified: " + this.version);
    }
}
