package io.sikt.iso8583.packager;

import io.sikt.iso8583.FieldType;
import io.sikt.iso8583.IsoException;
import io.sikt.iso8583.IsoMsg;
import io.sikt.iso8583.packager.fields.PackagerField;
import io.sikt.iso8583.util.ByteArrayUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public abstract class BasePackager implements MessagePackager {

    final Map<Integer, PackagerField> packagerInfo;
    final Map<String, List<Integer>> messageTypeParserGuide;
    Charset encoding;

    private static final int MTI_FIELD = 0;
    private static final int PRIMARY_BITMAP_FIELD = 1;

    protected BasePackager(PackagerConfiguration packagerConfiguration) {
        this.packagerInfo = packagerConfiguration.getPackagerInfo();
        this.messageTypeParserGuide = packagerConfiguration.getMessageTypeParserGuide();
        this.encoding = packagerConfiguration.getEncoding();
    }

    public void setPackagerField(int field, PackagerField value) {
        this.packagerInfo.put(field, value);
    }

    @Override
    public Charset getCharacterEncoding() {
        return this.encoding;
    }

    @Override
    public PackagerField getFieldPackager(int field) {
        return packagerInfo.get(field);
    }

    @Override
    public IsoMsg unpack(byte[] data) {

        if (data.length < 4)
            throw new IsoException("Message length (" + data.length + ") is to short! No MTI included");


        IsoMsg msg = new IsoMsg(this);
        AtomicInteger readOffset = new AtomicInteger(0);

        final String mti = readPackagerField(data, readOffset, packagerInfo.get(MTI_FIELD));
        msg.setMTI(mti);

        final BitSet bMap = readBitMap(data, readOffset, packagerInfo.get(PRIMARY_BITMAP_FIELD));
        log.debug("Unpacking mti: {}, bitmap: {}", mti, bMap);

        final Map<Integer, PackagerField> fieldParserGuide = getFieldsParserGuide(mti);
        validatePackagerField(mti, fieldParserGuide, MTI_FIELD);

        bMap.stream().forEach(field -> {
            validatePackagerField(mti, fieldParserGuide, field);
            msg.setField(field, readPackagerField(data, readOffset, packagerInfo.get(field)));
        });

        return msg;
    }

    @SneakyThrows
    @Override
    public byte[] pack(IsoMsg msg) {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        final String mti = msg.getField(MTI_FIELD);
        if (mti == null)
            log.debug("Unable to determine field parsing because field 0 (MTI) is not set.");

        final Map<Integer, PackagerField> fieldParserGuide = getFieldsParserGuide(mti);
        validatePackagerField(mti, fieldParserGuide, MTI_FIELD);

        //First MTI
        printToBuffer(msg, bout, fieldParserGuide.get(MTI_FIELD), MTI_FIELD);

        final BitSet bs = msg.getBitMap();

        writeBitMap(bout, bs, packagerInfo.get(PRIMARY_BITMAP_FIELD));

        if (msg.getIsoHeader() != null)
            bout.write(msg.getIsoHeader().getBytes(encoding.name()));

        bs.stream().forEach(field -> {
            validatePackagerField(mti, fieldParserGuide, field);

            printToBuffer(msg, bout, fieldParserGuide.get(field), field);
        });

        return bout.toByteArray();
    }

    private Map<Integer, PackagerField> getFieldsParserGuide(String mti) {
        final List<Integer> expectedFields = messageTypeParserGuide.get(mti);
        if (expectedFields == null || expectedFields.isEmpty())
            return packagerInfo;

        return packagerInfo.entrySet().stream()
            .filter(entry -> expectedFields.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void validatePackagerField(String mti, Map<Integer, PackagerField> fieldParserGuide, int field) {
        if (!fieldParserGuide.containsKey(field))
            throw new IsoException("Field " + field + " does not exist in packager for type " + mti + "!");
    }

    @SneakyThrows
    private void printToBuffer(IsoMsg msg, ByteArrayOutputStream bout, PackagerField packagerField, int field) {
        if (packagerField == null || !msg.hasField(field)) return;
        bout.write(packagerField.pack(msg.getField(field), encoding));
    }

    @SneakyThrows
    private void writeBitMap(ByteArrayOutputStream bout, BitSet bs, PackagerField bitMapField) {

        int len = bitMapField.getLength() >= 8 ? bs.length() + 62 >> 6 << 3 : bitMapField.getLength();

        final byte[] bitmap = ByteArrayUtil.bitSet2byte(bs, len);
        if (FieldType.isBinaryType(bitMapField.getType()))
            bout.write(bitmap);
        else
            bout.write(ByteArrayUtil.byte2hex(bitmap).getBytes());
    }

    @SneakyThrows
    private BitSet readBitMap(byte[] data, AtomicInteger offset, PackagerField bitMapField) {
        BitSet bmap = ByteArrayUtil.byte2BitSet(data, offset.get(), bitMapField.getLength() << 3);
        int len = bitMapField.getLength() >= 8 ? bmap.length() + 62 >> 6 << 3 : bitMapField.getLength();
        offset.addAndGet(len);
        return bmap;
    }

    private byte[] readChunk(byte[] what, AtomicInteger offset, int length) {
        int to = offset.get() + length;
        return Arrays.copyOfRange(what, offset.getAndAdd(length), to);
    }

    private String readPackagerField(byte[] what, AtomicInteger offset, PackagerField packagerField) {

        int length = packagerField.getLength();
        if (FieldType.isVariableLength(packagerField.getType())) {
            byte[] lengthRaw = readChunk(what, offset, packagerField.getType().getNumberOfLengthDigits());
            length = Integer.parseInt(new String(lengthRaw));
        }

        final byte[] tmp = readChunk(what, offset, length);
        return packagerField.unpack(tmp, encoding);
    }
}
