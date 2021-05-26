package io.sikt.iso8583.packager;

import io.sikt.iso8583.packager.fields.ALPHA;
import io.sikt.iso8583.packager.fields.LL_VAR;
import io.sikt.iso8583.packager.fields.NUMERIC;
import io.sikt.iso8583.packager.fields.PackagerField;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DummyPackager extends BasePackager {


    private Map<Integer, PackagerField> build1820() {
        return Stream.of(new Object[][]{
            {0, new NUMERIC(4, "MTI")},
            {7, new ALPHA(10, "DATE")},
            {11, new NUMERIC(6, "Systems trace audit number")},
            {12, new NUMERIC(12, "Date and time, Local transaction")},
            {24, new NUMERIC(3, "Function code")},
            {25, new NUMERIC(4, "Message reason code")},
            {32, new LL_VAR(11, "Acquirer institution identification code")},
            {53, new LL_VAR(48, "Security related control information")},
            {64, new ALPHA(8, "MAC")}
        }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (PackagerField) data[1]));

//        return new PackagerField[]{
//            /* 000 */new NUMERIC(4, "MTI"),
//            /* 001 */new NUMERIC(20, "Dummy field 0"),
//            /* 002 */new NUMERIC(20, "Dummy field 0"),
//            /* 003 */new ALPHA(20, "Dummy field 0"),
//            /* 004 */new ALPHA(20, "Dummy field 0"),
//            /* 005 */new ALPHA(20, "Dummy field 0"),
//            /* 006 */new ALPHA(20, "Dummy field 0")
//        };
    }

    @Override
    Map<String, Map<Integer, PackagerField>> buildMessagePackagers() {
        Map<String, Map<Integer, PackagerField>> packagerMap = new HashMap<>();

        packagerMap.put("1820", build1820());


        return packagerMap;
    }
}
