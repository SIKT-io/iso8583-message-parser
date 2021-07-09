package io.sikt.iso8583.packager.fields;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.packager.padder.NullPadder;

public class LLL_VAR extends GenericPackagerField {

    public LLL_VAR(int length, String description) {
        super(length, description, FieldType.LLLVAR, new NullPadder());
    }
}
