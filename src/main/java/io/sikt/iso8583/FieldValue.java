package io.sikt.iso8583;

import lombok.Getter;

public class FieldValue {

    @Getter
    private FieldType type;
    @Getter
    private byte[] value;
//    private int length;

    public FieldValue(FieldType type, byte[] value) {
        this.type = type;
        this.value = value;
    }


//    public String getStringValue() {
//        switch (type) {
//            case BINARY:
//                setBinaryValue(value);
//                break;
//        }
//    }
}
