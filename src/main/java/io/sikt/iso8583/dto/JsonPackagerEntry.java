package io.sikt.iso8583.dto;

import lombok.Data;

import java.util.List;

@Data
public class JsonPackagerEntry {
    private String packagerName;
    private final List<JsonPackagerField> fields;
}
