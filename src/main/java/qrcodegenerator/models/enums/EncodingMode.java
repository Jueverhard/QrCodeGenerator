package qrcodegenerator.models.enums;

public enum EncodingMode {
    NUMERIC,
    ALPHANUMERIC,
    BINARY,
    KANJI;

    /**
     * @param chainToRepresent The chain to represent in the QR Code.
     * @return The best encoding mode to apply.
     */
    public static EncodingMode selectBestMode(String chainToRepresent) {
        if (chainToRepresent.matches("\\d+")) {
            return NUMERIC;
        } else {
            // For simplicity, we'll consider everything else as BINARY for now
            return BINARY;
        }
    }
}
