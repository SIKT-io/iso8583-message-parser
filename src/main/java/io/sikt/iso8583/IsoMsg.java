package io.sikt.iso8583;

import io.sikt.iso8583.packager.MessagePackager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.BitSet;
import java.util.Map;
import java.util.TreeMap;

@NoArgsConstructor
public class IsoMsg {

    @Setter
    private MessagePackager packager;

    @Getter
    private String encoding = System.getProperty("file.encoding");
    @Getter
    @Setter
    private boolean binBitmap;
    private int etx = -1;

    @Setter
    @Getter
    private String isoHeader; //Optional

    private final Map<Integer, String> fields = new TreeMap<>();

    @Getter
    private final BitSet bitMap = new BitSet();


    public IsoMsg(MessagePackager packager) {
        this.packager = packager;
    }

    @SneakyThrows
    public void setMTI(String mti) {
        this.setField(0, mti);
    }

    public String getField(int field) {
        return this.fields.get(field);
    }

    @SneakyThrows
    public byte[] getFieldAsByteArray(int field) {
        return this.fields.get(field).getBytes(encoding);
    }

    public void setField(int field, String value) {
        this.fields.put(field, value);
        recalculateBitMap();
    }

    private void recalculateBitMap() {
        bitMap.clear();
        fields.keySet().stream()
            .filter(field -> field > 0)
            .forEach(bitMap::set);
    }

    public byte[] pack() {
        return packager.pack(this);
    }
}
