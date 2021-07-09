package io.sikt.iso8583.packager.fields;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.packager.padder.NullPadder;
import io.sikt.iso8583.packager.padder.ZeroLeftPadder;

public class BINARY extends GenericPackagerField {

    public BINARY(int length, String description) {
        super(length, description, FieldType.BINARY, new NullPadder());
    }
}
