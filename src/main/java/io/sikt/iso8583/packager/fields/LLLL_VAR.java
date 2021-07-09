package io.sikt.iso8583.packager.fields;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.packager.padder.NullPadder;

public class LLLL_VAR extends GenericPackagerField {

    public LLLL_VAR(int length, String description) {
        super(length, description, FieldType.LLLLVAR, new NullPadder());
    }
}
