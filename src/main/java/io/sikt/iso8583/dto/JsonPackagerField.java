package io.sikt.iso8583.dto;

import io.sikt.iso8583.packager.fields.*;
import lombok.Data;

@Data
public class JsonPackagerField {
    private int field;
    private String type;
    private int length;
    private String description;


    public GenericPackagerField convertToPackagerField() {
        switch (type.toUpperCase()) {
            case "NUMERIC":
                return new NUMERIC(length, description);
            case "ALPHA":
                return new ALPHA(length, description);
            case "BINARY":
                return new BINARY(length, description);
            case "LL_VAR":
                return new LL_VAR(length, description);
            case "LLL_VAR":
                return new LLL_VAR(length, description);
            case "LLLL_VAR":
                return new LLLL_VAR(length, description);
            case "LL_BIN":
                return new LL_BIN(length, description);
            case "LLL_BIN":
                return new LLL_BIN(length, description);
            case "LLLL_BIN":
                return new LLLL_BIN(length, description);
            default:
                throw new RuntimeException("Unknown field type: " + type);
        }
    }
}
