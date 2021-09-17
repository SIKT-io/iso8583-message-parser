package io.sikt.iso8583;

import io.sikt.iso8583.packager.MessagePackager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.BitSet;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

@NoArgsConstructor
public class IsoMsg {

    @Setter
    private MessagePackager packager;

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

//    @SneakyThrows
//    public byte[] getFieldAsByteArray(int field) {
//        return this.fields.get(field).getBytes(encoding);
//    }

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

    public String dumpMsgAsJson() {
        StringJoiner sb = new StringJoiner(",", "{", "}");
        fields.forEach((key, value) -> appendFieldToSb(sb, key, value));
        return sb.toString();
    }

    private void appendFieldToSb(StringJoiner joiner, int fieldNumb, String value) {
        joiner.add("\"" + fieldNumb + "\"" + ":\"" + value + "\"");
    }
}
