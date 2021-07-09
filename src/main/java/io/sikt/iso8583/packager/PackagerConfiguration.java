package io.sikt.iso8583.packager;

import io.sikt.iso8583.packager.fields.PackagerField;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PackagerConfiguration {
    private String fileName;
    private Map<String, List<Integer>> messageTypeParserGuide;
    private Map<Integer, PackagerField> packagerInfo;
}
