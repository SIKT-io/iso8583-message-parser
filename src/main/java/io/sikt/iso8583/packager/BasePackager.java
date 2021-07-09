package io.sikt.iso8583.packager;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.IsoMsg;
import io.sikt.iso8583.packager.fields.PackagerField;
import io.sikt.iso8583.util.ByteArrayUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class BasePackager implements MessagePackager {

    final Map<Integer, PackagerField> packagerInfo;
    final Map<String, List<Integer>> messageTypeParserGuide;

    public BasePackager(PackagerConfiguration packagerConfiguration) {
        this.packagerInfo = packagerConfiguration.getPackagerInfo();
        this.messageTypeParserGuide = packagerConfiguration.getMessageTypeParserGuide();
    }


//    void setFieldPackagers(Map<Integer, GenericFieldPackager> fieldPackagers) {
//        this.fieldPackagers = buildFieldPackagers();
//    }

    @SneakyThrows
    @Override
    public byte[] pack(IsoMsg msg) {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        final String mti = msg.getField(0);
        if (mti == null)
            throw new RuntimeException("Unable to determine field parsing because field 0 is not set. ");

        final List<Integer> expectedFields = messageTypeParserGuide.get(mti);

        final Map<Integer, PackagerField> fieldParserGuide = packagerInfo.entrySet().stream()
            .filter(entry -> expectedFields.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        validatePackagerField(mti, fieldParserGuide, 0);

        //First MTI
        writeToBuffer(bout, msg.getFieldAsByteArray(0), fieldParserGuide.get(0).getType());

        final BitSet bs = msg.getBitMap();

        writeBitMap(bout, bs, msg.isBinBitmap());

        if (msg.getIsoHeader() != null)
            bout.write(msg.getIsoHeader().getBytes(msg.getEncoding()));

        bs.stream().forEach(field -> {
            validatePackagerField(mti, fieldParserGuide, field);

            printToBuffer(msg, bout, fieldParserGuide.get(field), field);

//            writeToBuffer(bout, msg.getFieldAsByteArray(field), fieldParserGuide[field].getType());
        });

        return bout.toByteArray();
    }

    private void validatePackagerField(String mti, Map<Integer, PackagerField> fieldParserGuide, int field) {
        if (!fieldParserGuide.containsKey(field))
            throw new RuntimeException("Field " + field + " does not exist in packager for type " + mti + "!");
    }

    @SneakyThrows
    private void printToBuffer(IsoMsg msg, ByteArrayOutputStream bout, PackagerField packagerField, int field) {
        bout.write(packagerField.pack(msg.getField(field), msg.getEncoding()));
    }

    @SneakyThrows
    private void writeBitMap(ByteArrayOutputStream bout, BitSet bs, boolean isBinaryBitMap) {

        final byte[] bitmap = ByteArrayUtil.bitSet2byte(bs);
        if (isBinaryBitMap)
            bout.write(bitmap);
        else
            bout.write(ByteArrayUtil.byte2hex(bitmap).getBytes());

    }

    private void writeToBuffer(ByteArrayOutputStream bout, byte[] value, FieldType fieldType) {
        if (value == null)
            return;
        try {
            switch (fieldType) {
                case BINARY:
                    bout.write(ByteArrayUtil.byte2hex(value).getBytes());
                    break;

                case ALPHA:
                case NUMERIC:
                    bout.write(value);
                    break;

                default:
                    final boolean isBinary = FieldType.isBinaryType(fieldType);
                    if (FieldType.isVariableLength(fieldType))
                        writeLengthHeader(bout,
                            isBinary ? value.length : value.length / 2,
                            fieldType);

                    bout.write(isBinary ? ByteArrayUtil.byte2hex(value).getBytes() : value);
            }
        } catch (IOException ex) {
            log.error("Failed to write field to buffer..", ex);
        }
    }

    private void writeLengthHeader(ByteArrayOutputStream bout, int length, FieldType type) {

        if (!FieldType.isVariableLength(type))
            return;

        final int digits;
        if (type == FieldType.LLLLBIN || type == FieldType.LLLLVAR) {
            digits = 4;
        } else if (type == FieldType.LLLBIN || type == FieldType.LLLVAR) {
            digits = 3;
        } else {
            digits = 2;
        }

        if (digits == 4) {
            bout.write((length / 1000) + 48);
            bout.write(((length % 1000) / 100) + 48);
        } else if (digits == 3) {
            bout.write((length / 100) + 48);
        }
        if (length >= 10) {
            bout.write(((length % 100) / 10) + 48);
        } else {
            bout.write(48);
        }
        bout.write((length % 10) + 48);
    }
}
