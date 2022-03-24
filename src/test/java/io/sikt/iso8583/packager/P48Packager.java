package io.sikt.iso8583.packager;

import io.sikt.iso8583.packager.fields.*;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class P48Packager extends BasePackager {
    public P48Packager(Charset messageEncoding) {
        super(buildPackagerConfiguration(messageEncoding));
    }

    static PackagerConfiguration buildPackagerConfiguration(Charset messageEncoding) {
        Map<Integer, PackagerField> packagerInfo = Stream.of(new Object[][]{
            {0, new ALPHA(0, "PLACEHOLDER")},
            {1, new BINARY(8, "BITMAP")},
            {2, new ALPHA(20, "Hardware & software configuration")},
            {3, new ALPHA(2, "Language code")},
            {4, new NUMERIC(10, "Batch/sequence number")},
            {7, new NUMERIC(9, "Multiple transaction control")},
            {8, new LLL_VAR(250, "Customer data")},
            {9, new LL_VAR(37, "Track 2 for second card")},
            {10, new LL_VAR(76, "Track 1 for second card")},
            {14, new ALPHA(2, "PIN encryption methodology")},
            {33, new LL_VAR(104, "Track 3 for second card")},
            {37, new ALPHA(1, "Vehicle identification entry mode")},
            {38, new NUMERIC(1, "Pump linked indicator")},
            {39, new NUMERIC(10, "Delivery note number")},
            {40, new BINARY(4, "Encryption parameter")},
            {43, new NUMERIC(1, "Solution identifier")},
            {45, new LL_VAR(17, "VIB box identifier")},
            {46, new LL_VAR(17, "Truck VIN")},
            {48, new NUMERIC(12, "Timestamp")},
            {49, new LL_BIN(99, "VIB additional data")},
        }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (PackagerField) data[1]));

        PackagerConfiguration configuration = new PackagerConfiguration();
        configuration.setPackagerInfo(packagerInfo);
        configuration.setMessageTypeParserGuide(Collections.emptyMap());
        configuration.setEncoding(messageEncoding);
        return configuration;
    }
}
