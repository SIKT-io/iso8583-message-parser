package io.sikt.iso8583.packager.fields;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.packager.padder.NullPadder;

public class LLL_BIN extends GenericPackagerField {

    public LLL_BIN(int length, String description) {
        super(length, description, FieldType.LLLBIN, new NullPadder());
    }
}
