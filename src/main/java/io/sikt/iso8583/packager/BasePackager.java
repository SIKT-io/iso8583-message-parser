package io.sikt.iso8583.packager;

import io.sikt.iso8583.IsoException;
import io.sikt.iso8583.IsoMsg;
import io.sikt.iso8583.packager.fields.PackagerField;
import io.sikt.iso8583.util.ByteArrayUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
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


    @SneakyThrows
    @Override
    public byte[] pack(IsoMsg msg) {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        final String mti = msg.getField(0);
        if (mti == null)
            throw new IsoException("Unable to determine field parsing because field 0 is not set. ");

        final List<Integer> expectedFields = messageTypeParserGuide.get(mti);

        final Map<Integer, PackagerField> fieldParserGuide = packagerInfo.entrySet().stream()
            .filter(entry -> expectedFields.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        validatePackagerField(mti, fieldParserGuide, 0);

        //First MTI
        printToBuffer(msg, bout, fieldParserGuide.get(0), 0);

        final BitSet bs = msg.getBitMap();

        writeBitMap(bout, bs, msg.isBinBitmap());

        if (msg.getIsoHeader() != null)
            bout.write(msg.getIsoHeader().getBytes(msg.getEncoding()));

        bs.stream().forEach(field -> {
            validatePackagerField(mti, fieldParserGuide, field);

            printToBuffer(msg, bout, fieldParserGuide.get(field), field);
        });

        return bout.toByteArray();
    }

    private void validatePackagerField(String mti, Map<Integer, PackagerField> fieldParserGuide, int field) {
        if (!fieldParserGuide.containsKey(field))
            throw new IsoException("Field " + field + " does not exist in packager for type " + mti + "!");
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
}
