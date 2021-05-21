package io.sikt.iso8583.packager;

import io.sikt.iso8583.FieldValue;
import lombok.Data;

import java.io.IOException;
import java.io.ObjectOutput;

@Data
public abstract class FieldPackager {

    private int len;
    private String description;
    protected boolean pad;
    protected boolean trim;

    public FieldPackager(int len, String description) {
        this.len = len;
        this.description = description;
    }

    public abstract byte[] pack(FieldValue val);


    public void pack(FieldValue val, ObjectOutput out)
            throws IOException {
        out.write(pack(val));
    }
}
