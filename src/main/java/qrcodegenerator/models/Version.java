package qrcodegenerator.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Version {
    V1(21, 14),
    V2(25, 26),
    V3(29, 42),
    V4(33, 62),
    V5(37, 84),
    V6(41, 106),
    V7(45, 122),
    V8(49, 152),
    V9(53, 180),
    V10(57, 213),
    V11(61, 251),
    V12(65, 287),
    V13(69, 331),
    V14(73, 362),
    V15(77, 412),
    V16(81, 450),
    V17(85, 504),
    V18(89, 560),
    V19(93, 624),
    V20(97, 666),
    V21(101, 711),
    V22(105, 779),
    V23(109, 857),
    V24(113, 911),
    V25(117, 997),
    V26(121, 1059),
    V27(125, 1125),
    V28(129, 1190),
    V29(133, 1264),
    V30(137, 1370),
    V31(141, 1452),
    V32(145, 1538),
    V33(149, 1628),
    V34(153, 1722),
    V35(157, 1809),
    V36(161, 1911),
    V37(165, 1989),
    V38(169, 2099),
    V39(173, 2213),
    V40(177, 2331);

    @Getter
    private final int codeWidth;

    private final int maxBinaryChars;

    /**
     * @param chainSize    The size of the binary chain to encode.
     * @param encodingMode The encoding mode to use.
     * @return The minimal version that can encode the chain.
     */
    public static Version fromChainSize(int chainSize, EncodingMode encodingMode) {
        if (EncodingMode.BINARY != encodingMode) {
            throw new UnsupportedOperationException("Only BINARY encoding mode is supported for now");
        }
        return Arrays.stream(values())
                .filter(version -> version.maxBinaryChars >= chainSize)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The given binary chain is too big to be encoded into a QR Code"));
    }
}
