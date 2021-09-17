package io.sikt.iso8583.packager.fields;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.packager.padder.Padding;

import java.nio.charset.Charset;

public interface PackagerField {

    int getLength();

    String getDescription();

    FieldType getType();

    Padding getPadding();

    byte[] pack(String what, Charset charset);

    String unpack(byte[] what, Charset charset);
}
