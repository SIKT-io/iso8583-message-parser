package io.sikt.iso8583.packager.fields;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.packager.padder.Padding;
import io.sikt.iso8583.util.ByteArrayUtil;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

public abstract class GenericPackagerField implements PackagerField {

    private final int length;
    private final String description;
    private final Padding padding;
    private final FieldType type;

    public GenericPackagerField(int length, String description, FieldType fieldType, Padding padding) {
        this.length = length;
        this.description = description;
        this.padding = padding;
        this.type = fieldType;
    }

    @SneakyThrows
    public byte[] pack(String what, String charset) {

        final String padded = padding.pad(what, length);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        final boolean isBinary = FieldType.isBinaryType(type);
        final byte[] paddedRaw = isBinary ? ByteArrayUtil.hex2byte(what, Charset.forName(charset)) : padded.getBytes(charset);
        if (FieldType.isVariableLength(type)) {
            writeLengthHeader(bout, isBinary ? paddedRaw.length * 2 : paddedRaw.length);
        }

        bout.write(paddedRaw);

        return bout.toByteArray();
    }

    @SneakyThrows
    public String unpack(byte[] what, String charset) {

        final boolean isBinary = FieldType.isBinaryType(type);

        if (isBinary) {
            what = ByteArrayUtil.byte2hex(what).getBytes(charset);
        }

        String padded = new String(what, charset);

        return padding.unpad(padded);
    }

    private void writeLengthHeader(ByteArrayOutputStream bout, int length) {

        if (!FieldType.isVariableLength(type))
            return;

        if (type.getNumberOfLengthDigits() == 4) {
            bout.write((length / 1000) + 48);
            bout.write(((length % 1000) / 100) + 48);
        } else if (type.getNumberOfLengthDigits() == 3) {
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

    @Override
    public String toString() {
        return "GenericPackagerField{" +
            "length=" + length +
            ", description='" + description + '\'' +
            ", padding=" + padding.getClass().getSimpleName() +
            ", type=" + type +
            '}';
    }
}
