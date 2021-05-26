package io.sikt.iso8583.packager.fields;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.packager.padder.SpaceRightPadder;

public class ALPHA extends GenericPackagerField {

    public ALPHA(int length, String description) {
        super(length, description, FieldType.ALPHA, new SpaceRightPadder());
    }
}
