package io.sikt.iso8583;

import io.sikt.iso8583.packager.DummyPackager;
import org.junit.jupiter.api.Test;

public class MessageParserTest {

    @Test
    void messageParser() {
        IsoMsg msg = new IsoMsg();
        msg.setPackager(new DummyPackager());
        msg.setBinBitmap(false);
        msg.setMTI("1100");

        msg.setField(1, FieldType.ALPHA.value("test".getBytes()));
        msg.setField(2, FieldType.ALPHA.value("test".getBytes()));
        msg.setField(3, FieldType.NUMERIC.value("test".getBytes()));
        msg.setField(4, FieldType.BINARY.value("".getBytes()));
        msg.setField(5, FieldType.LLVAR.value("æøå".getBytes()));
        msg.setField(6, FieldType.LLBIN.value("".getBytes()));

        System.out.println(msg.getBitMap());

        System.out.println(new String(msg.pack()));
    }
}
