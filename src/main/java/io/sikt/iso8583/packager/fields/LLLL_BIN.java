package io.sikt.iso8583.packager.fields;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.packager.padder.NullPadder;

public class LLLL_BIN extends GenericPackagerField {

    public LLLL_BIN(int length, String description) {
        super(length, description, FieldType.LLLLBIN, new NullPadder());
    }
}
