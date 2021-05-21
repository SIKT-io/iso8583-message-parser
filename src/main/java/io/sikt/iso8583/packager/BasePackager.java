package io.sikt.iso8583.packager;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.FieldValue;
import io.sikt.iso8583.IsoMsg;
import io.sikt.iso8583.util.ByteArrayUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;

@Slf4j
public abstract class BasePackager implements MessagePackager {

    @SneakyThrows
    @Override
    public byte[] pack(IsoMsg msg) {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        //First MTI
        writeToBuffer(bout, msg.getField(0));

        final BitSet bs = msg.getBitMap();

        writeBitMap(bout, bs, msg.isBinBitmap());

        if (msg.getIsoHeader() != null)
            bout.write(msg.getIsoHeader().getBytes(msg.getEncoding()));

        bs.stream().forEach(field -> writeToBuffer(bout, msg.getField(field)));

        return bout.toByteArray();
    }

    @SneakyThrows
    private void writeBitMap(ByteArrayOutputStream bout, BitSet bs, boolean isBinaryBitMap) {

        final byte[] bitmap = ByteArrayUtil.bitSet2byte(bs);
        if (isBinaryBitMap)
            bout.write(bitmap);
        else
            bout.write(ByteArrayUtil.byte2hex(bitmap).getBytes());

    }

    private void writeToBuffer(ByteArrayOutputStream bout, FieldValue value) {
        if (value == null || value.getValue() == null)
            return;
        try {
            switch (value.getType()) {
                case BINARY:
                    bout.write(ByteArrayUtil.byte2hex(value.getValue()).getBytes());
                    break;

                case ALPHA:
                case NUMERIC:
                    bout.write(value.getValue());
                    break;

                default:
                    final boolean isBinary = FieldType.isBinaryType(value.getType());
                    if (FieldType.isVariableLength(value.getType()))
                        writeLengthHeader(bout,
                                isBinary ? value.getValue().length : value.getValue().length / 2,
                                value.getType());

                    bout.write(isBinary ? ByteArrayUtil.byte2hex(value.getValue()).getBytes() : value.getValue());
            }
        } catch (IOException ex) {
            log.error("Failed to write field to buffer..", ex);
        }
    }

    private void writeLengthHeader(ByteArrayOutputStream bout, int length, FieldType type) {

        if (!FieldType.isVariableLength(type))
            return;

        final int digits;
        if (type == FieldType.LLLLBIN || type == FieldType.LLLLVAR) {
            digits = 4;
        } else if (type == FieldType.LLLBIN || type == FieldType.LLLVAR) {
            digits = 3;
        } else {
            digits = 2;
        }

        if (digits == 4) {
            bout.write((length / 1000) + 48);
            bout.write(((length % 1000) / 100) + 48);
        } else if (digits == 3) {
            bout.write((length / 100) + 48);
        }
        if (length >= 10) {
            bout.write(((length % 100) / 10) + 48);
        } else {
            bout.write(48);
        }
        bout.write((length % 10) + 48);
    }
}
