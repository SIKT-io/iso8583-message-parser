package io.sikt.iso8583;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum FieldType {
    NUMERIC,
    ALPHA,
    BINARY,
    LLVAR,
    LLBIN,
    LLLVAR,
    LLLBIN,
    LLLLVAR,
    LLLLBIN;

    public static final Set<FieldType> VARIABLE_LENGTH_TYPES = Collections.unmodifiableSet(EnumSet.of(LLBIN, LLLBIN, LLLLBIN, LLVAR, LLLVAR, LLLLVAR));
    public static final Set<FieldType> BINARY_TYPE = Collections.unmodifiableSet(EnumSet.of(BINARY, LLBIN, LLLBIN, LLLLBIN));


    public static boolean isBinaryType(FieldType fieldType) {
        return BINARY_TYPE.contains(fieldType);
    }

    public static boolean isVariableLength(FieldType fieldType) {
        return VARIABLE_LENGTH_TYPES.contains(fieldType);
    }


    public FieldValue value(byte[] value) {
        return new FieldValue(this, value);
    }
}
