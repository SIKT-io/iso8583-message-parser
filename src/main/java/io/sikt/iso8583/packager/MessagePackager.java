package io.sikt.iso8583.packager;

import io.sikt.iso8583.IsoMsg;
import io.sikt.iso8583.packager.fields.PackagerField;

import java.nio.charset.Charset;

public interface MessagePackager {

    PackagerField getFieldPackager(int field);

    byte[] pack(IsoMsg msg);

    IsoMsg unpack(byte[] data);

    Charset getCharacterEncoding();

}
