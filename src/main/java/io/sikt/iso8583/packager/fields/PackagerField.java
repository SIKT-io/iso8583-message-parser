package io.sikt.iso8583.packager.fields;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.packager.padder.Padding;

public interface PackagerField {

    int getLength();

    String getDescription();

    FieldType getType();

    Padding getPadding();

    byte[] pack(String what, String charset);

    String unpack(byte[] what, String charset);
}
