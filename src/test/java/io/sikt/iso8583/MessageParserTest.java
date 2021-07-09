package io.sikt.iso8583;

import io.sikt.iso8583.packager.DummyPackager;
import io.sikt.iso8583.packager.GenericPackager;
import io.sikt.iso8583.util.ByteArrayUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageParserTest {

    @Test
    void networkMgmtPackagerTest_genericPackager() {
        final String expected = "3138323002300101000000003130323431303336303030313030303131373130323431303336303038333130353130353234";
        IsoMsg msg = new IsoMsg();
        msg.setPackager(new GenericPackager("src/main/resources/packagers/IFSF-ASCII-1993.json"));
        msg.setBinBitmap(true);
        msg.setMTI("1820");

        msg.setField(7, "1024103600");
        msg.setField(11, "010001");
        msg.setField(12, "171024103600");
        msg.setField(24, "831");
        msg.setField(32, "10524");

        System.out.println(msg.getBitMap());

        final byte[] packed = msg.pack();
        final String hexed = ByteArrayUtil.byte2hex(packed);
        System.out.println(new String(packed));
        System.out.println(hexed);

        Assertions.assertEquals(expected, hexed);
    }

    @Test
    void networkMgmtPackagerTest_dummyPackager() {

        final String expected = "3138323002300101000000003130323431303336303030313030303131373130323431303336303038333130353130353234";

        IsoMsg msg = new IsoMsg();
        msg.setPackager(new DummyPackager());
        msg.setBinBitmap(true);
        msg.setMTI("1820");

        msg.setField(7, "1024103600");
//        msg.setField(11, "1024103600");
        msg.setField(11, "010001");
        msg.setField(12, "171024103600");
        msg.setField(24, "831");
        msg.setField(32, "10524");

        System.out.println(msg.getBitMap());

        final byte[] packed = msg.pack();
        final String hexed = ByteArrayUtil.byte2hex(packed);
        System.out.println(new String(packed));
        System.out.println(hexed);

        Assertions.assertEquals(expected, hexed);

        //31383230023001010000000031303234313033363030303130303031313731303234313033363030383331 30353130353234
        //31383230023001010000000031303234313033363030303130303031313731303234313033363030383331 30323130353234


        //1100FC00000000000000testtesttestæøå
        //1100FC000000000000000000000000000000test0000000000000000testtest                                   æøå                 

        //31383230023001 810000000031303234313033313032343130333630303030303030303031303030313137313032343130333630303038333130353130353234202020202020
        //31383230023001 01000000003130323431303336303030313030303131373130323431303336303038333130353130353234

        //313832300230010100000000313032343130333 0313030 303131373130323431303336303038333130353130353234202020202020
        //313832300230010100000000313032343130333 6303030 313030303131373130323431303336303038333130353130353234


        //10241036000100011710241036008310510524
        //10241030100011710241036008310510524
    }
}
