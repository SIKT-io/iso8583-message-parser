package io.sikt.iso8583.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ByteArrayUtil {

    public static byte[] hex2byte(String s, Charset charset) {
        if (charset == null) charset = StandardCharsets.ISO_8859_1;
        if (s.length() % 2 == 0)
            return hex2byte(s.getBytes(charset), 0, s.length() >> 1);
        else
            return hex2byte("0" + s, charset);

    }

    public static byte[] hex2byte(byte[] b, int offset, int len) {
        byte[] d = new byte[len];
        for (int i = 0; i < len * 2; i++) {
            int shift = i % 2 == 1 ? 0 : 4;
            d[i >> 1] |= Character.digit((char) b[offset + i], 16) << shift;
        }
        return d;
    }

    public static byte[] concat(byte[]... arrays) {
        int totalLength = Arrays.stream(arrays).mapToInt(array -> array.length).reduce(0, Integer::sum);
        int offset = 0;

        byte[] concatArray = new byte[totalLength];

        for (byte[] array : arrays) {
            System.arraycopy(array, 0, concatArray, offset, array.length);
            offset += array.length;
        }

        return concatArray;
    }

    public static String byte2hex(byte[] bs) {
        return byte2hex(bs, 0, bs.length);
    }

    public static String byte2hex(byte[] bs, int off, int length) {
        if (bs.length > off && bs.length >= off + length) {
            StringBuilder sb = new StringBuilder(length * 2);
            byte2hexAppend(bs, off, length, sb);
            return sb.toString().toUpperCase();
        } else {
            return "";
        }
    }

    private static void byte2hexAppend(byte[] bs, int off, int length, StringBuilder sb) {
        if (bs.length > off && bs.length >= off + length) {
            sb.ensureCapacity(sb.length() + length * 2);

            for (int i = off; i < off + length; ++i) {
                sb.append(Character.forDigit(bs[i] >>> 4 & 15, 16));
                sb.append(Character.forDigit(bs[i] & 15, 16));
            }

        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * converts a BitSet into a binary field
     * used in pack routines
     * <p>
     * This method will set bits 0 (and 65) if there's a secondary (and tertiary) bitmap
     * (i.e., if the bitmap length is > 64 (and > 128))
     *
     * @param b - the BitSet
     * @return binary representation
     */
    public static byte[] bitSet2byte(BitSet b, int bytes) {
        int len = bytes * 8;
        byte[] d = new byte[bytes];
        for (int i = 0; i < len; i++)
            if (b.get(i + 1)) // +1 because we don't use bit 0 of the BitSet
                d[i >> 3] |= 0x80 >> i % 8;
        if (len > 64)
            d[0] |= 0x80;
        if (len > 128)
            d[8] |= 0x80;
        return d;
    }


    /**
     * Converts a binary representation of a Bitmap field
     * into a Java BitSet
     *
     * @param b       - binary representation
     * @param offset  - staring offset
     * @param maxBits - max number of bits (supports 64,128 or 192)
     * @return java BitSet object
     */
    public static BitSet byte2BitSet(byte[] b, int offset, int maxBits) {
        boolean b1 = (b[offset] & 0x80) == 0x80;
        boolean b65 = (b.length > offset + 8) && ((b[offset + 8] & 0x80) == 0x80);

        int len = (maxBits > 128 && b1 && b65) ? 192 :
            (maxBits > 64 && b1) ? 128 :
                (maxBits < 64) ? maxBits : 64;

        BitSet bmap = new BitSet(len);
        for (int i = 0; i < len; i++)
            if ((b[offset + (i >> 3)] & 0x80 >> i % 8) > 0)
                bmap.set(i + 1);
        return bmap;
    }
}
