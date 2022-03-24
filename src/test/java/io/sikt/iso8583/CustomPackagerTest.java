package io.sikt.iso8583;

import io.sikt.iso8583.packager.P48Packager;
import io.sikt.iso8583.util.ByteArrayUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class CustomPackagerTest {

    @Test
    void unpackP48() {
        final String expected = "0180000000000000303139303136323130303134305C34343836333432303337313333373030303030303030303030303030313D3233313230303030313930313130303030";

        IsoMsg msg = new IsoMsg();
        msg.setPackager(new P48Packager(StandardCharsets.ISO_8859_1));
        msg.setField(8, "0162100140\\44863420"); //Customer data
        msg.setField(9, "1337000000000000001=23120000190110000"); //Second track 2

        final byte[] packed = msg.pack();
        final String hexed = ByteArrayUtil.byte2hex(packed);

        Assertions.assertEquals(expected, hexed);
    }

}
