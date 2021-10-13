package io.sikt.iso8583;

import io.sikt.iso8583.packager.DummyPackager;
import io.sikt.iso8583.packager.GenericPackager;
import io.sikt.iso8583.util.ByteArrayUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class MessageParserTest {

    private static final String GENERIC_PACKAGER_PATH = "src/main/resources/packagers/IFSF-ASCII-1993.json";
    private static final Charset MESSAGE_ENCODING = StandardCharsets.ISO_8859_1;

    @Test
    void networkMgmtPackagerTest_genericPackager() {
        final String expected = "3138323002300101000008013130323431303336303030313030303131373130323431303336303038333130353130353234333451019A240F6DE12F73A92250DDE3D675B40F2E47C392DF342F862410524261D5C8DBC38CBF94D206626C";
        IsoMsg msg = new IsoMsg();
        msg.setPackager(new GenericPackager(GENERIC_PACKAGER_PATH).setEncoding(MESSAGE_ENCODING));
        msg.setMTI("1820");

        msg.setField(7, "1024103600");
        msg.setField(11, "010001");
        msg.setField(12, "171024103600");
        msg.setField(24, "831");
        msg.setField(32, "10524");
        msg.setField(53, "51019A240F6DE12F73A92250DDE3D675B40F2E47C392DF342F862410524261D5C8DB");
        msg.setField(64, "C38CBF94D206626C");

        final byte[] packed = msg.pack();
        final String hexed = ByteArrayUtil.byte2hex(packed);

        Assertions.assertEquals(expected, hexed);
    }

    @Test
    void networkMgmtPackagerTest_dummyPackager() {

        final String expected = "3138323002300101000000003130323431303336303030313030303131373130323431303336303038333130353130353234";

        IsoMsg msg = new IsoMsg();
        msg.setPackager(new DummyPackager(MESSAGE_ENCODING));
        msg.setMTI("1820");

        msg.setField(7, "1024103600");
        msg.setField(11, "010001");
        msg.setField(12, "171024103600");
        msg.setField(24, "831");
        msg.setField(32, "10524");

        final byte[] packed = msg.pack();
        final String hexed = ByteArrayUtil.byte2hex(packed);
        Assertions.assertEquals(expected, hexed);

    }

    @Test
    void unpackNetworkManagementRequest_dummyPackager() {
        final byte[] expected = ByteArrayUtil.hex2byte("3138323002300101000000003130323431303336303030313030303131373130323431303336303038333130353130353234", MESSAGE_ENCODING);
        IsoMsg msg = new DummyPackager(MESSAGE_ENCODING).unpack(expected);

        Assertions.assertEquals("1024103600", msg.getField(7));
        Assertions.assertEquals("010001", msg.getField(11));
        Assertions.assertEquals("171024103600", msg.getField(12));
        Assertions.assertEquals("831", msg.getField(24));
        Assertions.assertEquals("10524", msg.getField(32));

        Assertions.assertEquals(ByteArrayUtil.byte2hex(expected), ByteArrayUtil.byte2hex(msg.pack()));

    }

    @Test
    void unpackNetworkManagementRequest_genericPackager() {
        final byte[] expected = ByteArrayUtil.hex2byte("3138323002300101000000003130323431303336303030313030303131373130323431303336303038333130353130353234", StandardCharsets.ISO_8859_1);
        IsoMsg msg = new GenericPackager(GENERIC_PACKAGER_PATH)
            .setEncoding(MESSAGE_ENCODING)
            .unpack(expected);

        Assertions.assertEquals("1024103600", msg.getField(7));
        Assertions.assertEquals("010001", msg.getField(11));
        Assertions.assertEquals("171024103600", msg.getField(12));
        Assertions.assertEquals("831", msg.getField(24));
        Assertions.assertEquals("10524", msg.getField(32));

        Assertions.assertEquals(ByteArrayUtil.byte2hex(expected), ByteArrayUtil.byte2hex(msg.pack()));
    }

}
