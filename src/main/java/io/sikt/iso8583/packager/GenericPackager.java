package io.sikt.iso8583.packager;

import io.sikt.iso8583.dto.JsonPackagerParser;

import java.nio.charset.Charset;

public class GenericPackager extends BasePackager {

    public GenericPackager(String filePath) {
        super(JsonPackagerParser.readPackagerFile(filePath));
    }

    public GenericPackager setEncoding(Charset charset) {
        super.encoding = charset;
        return this;
    }
}
