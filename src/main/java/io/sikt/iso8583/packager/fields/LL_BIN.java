package io.sikt.iso8583.packager.fields;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.packager.padder.NullPadder;

public class LL_BIN extends GenericPackagerField {

    public LL_BIN(int length, String description) {
        super(length, description, FieldType.LLBIN, new NullPadder());
    }
}
