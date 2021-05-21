package io.sikt.iso8583.packager;

import io.sikt.iso8583.IsoMsg;

public interface MessagePackager {

    byte[] pack(IsoMsg msg);
}
