package io.sikt.iso8583.dto;

import io.sikt.iso8583.packager.PackagerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JsonPackagerParserTest {

    @Test
    void readPackagersFromFile() {
        PackagerConfiguration configuration = JsonPackagerParser.readPackagerFile("src/main/resources/packagers/IFSF-ASCII-1993.json");
        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.getPackagerInfo());
        Assertions.assertNotNull(configuration.getMessageTypeParserGuide());
        System.out.println(configuration);
    }
}
