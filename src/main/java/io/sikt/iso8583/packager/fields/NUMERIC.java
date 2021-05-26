package io.sikt.iso8583.packager.fields;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.packager.padder.ZeroLeftPadder;

public class NUMERIC extends GenericPackagerField {

    public NUMERIC(int length, String description) {
        super(length, description, FieldType.NUMERIC, new ZeroLeftPadder());
    }
}
