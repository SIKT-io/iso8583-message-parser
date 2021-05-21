package io.sikt.iso8583.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
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

    public static byte[] int2byte(int value) {
        if (value < 0) {
            return new byte[]{(byte) (value >>> 24 & 255), (byte) (value >>> 16 & 255), (byte) (value >>> 8 & 255), (byte) (value & 255)};
        } else if (value <= 255) {
            return new byte[]{(byte) (value & 255)};
        } else if (value <= 65535) {
            return new byte[]{(byte) (value >>> 8 & 255), (byte) (value & 255)};
        } else {
            return value <= 16777215 ? new byte[]{(byte) (value >>> 16 & 255), (byte) (value >>> 8 & 255), (byte) (value & 255)} : new byte[]{(byte) (value >>> 24 & 255), (byte) (value >>> 16 & 255), (byte) (value >>> 8 & 255), (byte) (value & 255)};
        }
    }

    public static int byte2int(byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);

            int i;
            for (i = 0; i < 4 - bytes.length; ++i) {
                byteBuffer.put((byte) 0);
            }

            for (i = 0; i < bytes.length; ++i) {
                byteBuffer.put(bytes[i]);
            }

            byteBuffer.position(0);
            return byteBuffer.getInt();
        } else {
            return 0;
        }
    }

    /**
     * Bitwise XOR between corresponding bytes
     *
     * @param op1 byteArray1
     * @param op2 byteArray2
     * @return an array of length = the smallest between op1 and op2
     */
    public static byte[] xor(byte[] op1, byte[] op2) {
        byte[] result;
        // Use the smallest array
        if (op2.length > op1.length) {
            result = new byte[op1.length];
        } else {
            result = new byte[op2.length];
        }
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (op1[i] ^ op2[i]);
        }
        return result;
    }


    /**
     * DES Keys use the LSB as the odd parity bit.  This method can
     * be used enforce correct parity.
     *
     * @param bytes the byte array to set the odd parity on.
     */
    public static void adjustDESParity(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i];
            bytes[i] = (byte) (b & 0xfe | (b >> 1 ^ b >> 2 ^ b >> 3 ^ b >> 4 ^ b >> 5 ^ b >> 6 ^ b >> 7 ^ 0x01) & 0x01);
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
    public static byte[] bitSet2byte(BitSet b) {
        int len = b.length() + 62 >> 6 << 6;        // +62 because we don't use bit 0 in the BitSet
        byte[] d = new byte[len >> 3];
        for (int i = 0; i < len; i++)
            if (b.get(i + 1))                     // +1 because we don't use bit 0 of the BitSet
                d[i >> 3] |= 0x80 >> i % 8;
        if (len > 64)
            d[0] |= 0x80;
        if (len > 128)
            d[8] |= 0x80;
        return d;
    }
}
