package io.sikt.iso8583.packager.fields;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.packager.padder.Padding;
import io.sikt.iso8583.util.ByteArrayUtil;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;

public abstract class GenericPackagerField implements PackagerField {

    private int length;
    private String description;
    private Padding padding;
    private FieldType type;

    public GenericPackagerField(int length, String description, FieldType fieldType, Padding padding) {
        this.length = length;
        this.description = description;
        this.padding = padding;
        this.type = fieldType;
    }

    @SneakyThrows
    public byte[] pack(String what, String charset) {

        final boolean isBinary = FieldType.isBinaryType(type);


        if (isBinary) {
            what = ByteArrayUtil.byte2hex(what.getBytes(charset));
        }

        final String padded = padding.pad(what, length);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        final byte[] paddedRaw = padded.getBytes(charset);
        if (FieldType.isVariableLength(type)) {
            writeLengthHeader(bout, isBinary ? paddedRaw.length * 2 : paddedRaw.length, type);
        }

        bout.write(paddedRaw);

        return bout.toByteArray();
    }

    @SneakyThrows
    public String unpack(byte[] what, String charset) {
        final String padded = new String(what, charset);
        return padding.unpad(padded);
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

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public FieldType getType() {
        return type;
    }

    @Override
    public Padding getPadding() {
        return padding;
    }
}
