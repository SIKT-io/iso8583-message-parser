package io.sikt.iso8583.packager;

import io.sikt.iso8583.packager.fields.GenericFieldPackager;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MessageTypePackager {

    private String mti;
    private List<GenericFieldPackager> packagers;
}
