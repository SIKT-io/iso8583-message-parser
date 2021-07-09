package io.sikt.iso8583.packager;

import io.sikt.iso8583.dto.JsonPackagerParser;

public class GenericPackager extends BasePackager {

    public GenericPackager(String filePath) {
        super(JsonPackagerParser.readPackagerFile(filePath));
    }

}
