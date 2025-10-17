package qrcodegenerator;

import qrcodegenerator.models.QrCode;

public class QrCodeGenerator {
    public static void main(String[] args) {
        QrCode qrCode = new QrCode(args[0]);
        qrCode.print();
    }
}