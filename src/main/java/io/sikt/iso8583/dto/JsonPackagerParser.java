package io.sikt.iso8583.dto;

import com.grack.nanojson.JsonParserException;
import com.grack.nanojson.JsonReader;
import io.sikt.iso8583.IsoException;
import io.sikt.iso8583.packager.PackagerConfiguration;
import io.sikt.iso8583.packager.fields.PackagerField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonPackagerParser {

    public static PackagerConfiguration readPackagerFile(String path) {
        final long timer = System.nanoTime();
        PackagerConfiguration configuration = new PackagerConfiguration();
        try {
            final Path filePath = Paths.get(path);
            configuration.setFileName(filePath.getFileName().toString());
            configuration.setFileName(configuration.getFileName().substring(0, configuration.getFileName().lastIndexOf(".")));
            InputStream stream = Files.newInputStream(filePath);
            final JsonReader reader = JsonReader.from(stream);
            reader.object();
            reader.next();
            readJsonElement(configuration, reader);
            reader.next();
            readJsonElement(configuration, reader);

            if (log.isTraceEnabled())
                log.trace("imported PackagerConfiguration: {}", configuration);
        } catch (Exception ex) {
            log.error("Failed to parse packager-file: {}", path, ex);
        } finally {
            log.debug("readPackagersFromFile ({}) finished in {} ms", configuration.getFileName(), (System.nanoTime() - timer) / 1000000.0D);
        }
        return configuration;
    }

    private static void readJsonElement(PackagerConfiguration configuration, JsonReader reader) throws JsonParserException {
        switch (reader.key()) {
            case "messageTypes":
                configuration.setMessageTypeParserGuide(readFieldParseGuide(reader));
                break;
            case "packager":
                configuration.setPackagerInfo(readPackagerInfo(reader));
                break;
            default:
                throw new IsoException("Unexpected JSON element: " + reader.key());
        }
    }

    private static Map<Integer, PackagerField> readPackagerInfo(JsonReader reader) throws JsonParserException {
        reader.object();

        final List<JsonPackagerField> fields = getFields(reader);
        return fields.stream().collect(Collectors.toMap(JsonPackagerField::getField, JsonPackagerField::convertToPackagerField));
    }

    private static Map<String, List<Integer>> readFieldParseGuide(JsonReader reader) throws JsonParserException {
        reader.object();
        final Map<String, List<Integer>> fieldParserGuide = new HashMap<>();
        while (reader.next()) {
            final String mti = reader.key();
            reader.array();
            final List<Integer> fields = new ArrayList<>();
            while (reader.next())
                fields.add(reader.intVal());
            fieldParserGuide.put(mti, fields);
        }
        return fieldParserGuide;
    }

    private static List<JsonPackagerField> getFields(JsonReader reader) throws JsonParserException {
        final List<JsonPackagerField> fields = new ArrayList<>();
        reader.next();
        reader.array();
        while (reader.next()) {
            JsonPackagerField field = new JsonPackagerField();
            reader.object();
            reader.next();
            field.setField(reader.intVal());
            reader.next();
            field.setType(reader.string());
            reader.next();
            field.setLength(reader.intVal());
            reader.next();
            field.setDescription(reader.string());
            reader.next();
            fields.add(field);
        }
        return fields;
    }
}
