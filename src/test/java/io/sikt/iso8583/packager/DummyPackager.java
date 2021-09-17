package io.sikt.iso8583.packager;

import io.sikt.iso8583.packager.fields.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DummyPackager extends BasePackager {

    public DummyPackager(Charset messageEncoding) {
        super(buildPackagerConfiguration(messageEncoding));
    }

    static PackagerConfiguration buildPackagerConfiguration(Charset messageEncoding) {
        Map<Integer, PackagerField> packagerInfo = Stream.of(new Object[][]{
            {0, new NUMERIC(4, "MTI")},
            {1, new BINARY(8, "BITMAP")},
            {7, new ALPHA(10, "DATE")},
            {11, new NUMERIC(6, "Systems trace audit number")},
            {12, new NUMERIC(12, "Date and time, Local transaction")},
            {24, new NUMERIC(3, "Function code")},
            {25, new NUMERIC(4, "Message reason code")},
            {32, new LL_VAR(11, "Acquirer institution identification code")},
            {53, new LL_VAR(48, "Security related control information")},
            {64, new BINARY(8, "MAC")}
        }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (PackagerField) data[1]));

        PackagerConfiguration configuration = new PackagerConfiguration();
        configuration.setPackagerInfo(packagerInfo);
        configuration.setMessageTypeParserGuide(Collections.singletonMap("1820", new ArrayList<>(packagerInfo.keySet())));
        configuration.setEncoding(messageEncoding);
        return configuration;
    }
}
