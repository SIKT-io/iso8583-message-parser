package io.sikt.iso8583.packager.fields;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.packager.padder.NullPadder;

public class LL_VAR extends GenericPackagerField {

    public LL_VAR(int length, String description) {
        super(length, description, FieldType.LLVAR, new NullPadder());
    }
}
