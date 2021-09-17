package io.sikt.iso8583;

import lombok.Getter;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum FieldType {
    NUMERIC(0),
    ALPHA(0),
    BINARY(0),
    LLVAR(2),
    LLBIN(2),
    LLLVAR(3),
    LLLBIN(3),
    LLLLVAR(4),
    LLLLBIN(4);

    @Getter
    private final int numberOfLengthDigits;

    FieldType(int numberOfLengthDigits) {
        this.numberOfLengthDigits = numberOfLengthDigits;
    }

    public static final Set<FieldType> VARIABLE_LENGTH_TYPES = Collections.unmodifiableSet(EnumSet.of(LLBIN, LLLBIN, LLLLBIN, LLVAR, LLLVAR, LLLLVAR));
    public static final Set<FieldType> BINARY_TYPE = Collections.unmodifiableSet(EnumSet.of(BINARY, LLBIN, LLLBIN, LLLLBIN));


    public static boolean isBinaryType(FieldType fieldType) {
        return BINARY_TYPE.contains(fieldType);
    }

    public static boolean isVariableLength(FieldType fieldType) {
        return VARIABLE_LENGTH_TYPES.contains(fieldType);
    }
}
