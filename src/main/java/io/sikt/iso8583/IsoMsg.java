package io.sikt.iso8583;

import io.sikt.iso8583.packager.MessagePackager;
import io.sikt.iso8583.packager.fields.PackagerField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.BitSet;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

@NoArgsConstructor
public class IsoMsg {

    @Setter
    @Getter
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

    public void setMTI(String mti) {
        this.setField(0, mti);
    }

    public String getMTI() {
        return this.getField(0);
    }

    public boolean hasField(int field) {
        return this.getField(field) != null;
    }

    public String getField(int field) {
        final String what = this.fields.get(field);

        // Pad numeric values
        if (what != null && what.length() == 0) {
            PackagerField fieldPackager = this.packager.getFieldPackager(field);
            if (fieldPackager != null && FieldType.NUMERIC.equals(fieldPackager.getType())) {
                return pad(fieldPackager, what);
            }
        }
        return what;
    }

    public byte[] getFieldAsByteArray(int field) {
        return this.fields.get(field).getBytes(packager.getCharacterEncoding());
    }

    public void removeFields(int... fields) {
        for (int field : fields)
            this.fields.remove(field);
        recalculateBitMap();
    }

    public void setField(int field, String value) {
        if (value != null)
            this.fields.put(field, value);
        else
            this.fields.remove(field);
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

    /**
     * Print IsoMsg as a Json-string
     *
     * @param paramsToMask Optional list of values that wil be masked with ***'s
     * @return
     */
    public String dumpMsgAsJson(String... paramsToMask) {
        StringJoiner sb = new StringJoiner(",", "{", "}");
        fields.forEach((key, value) -> appendFieldToSb(sb, key, value));

        String json = sb.toString();
        if (paramsToMask != null)
            for (String mask : paramsToMask)
                json = mask(mask, json);
        return json;
    }

    @Override
    public IsoMsg clone() {
        IsoMsg msg = new IsoMsg();
        msg.setMTI(this.getMTI());
        msg.setPackager(this.packager);
        msg.setIsoHeader(this.getIsoHeader());
        this.fields.forEach(msg::setField);
        return msg;
    }

    private void appendFieldToSb(StringJoiner joiner, int fieldNumb, String value) {
        joiner.add("\"" + fieldNumb + "\"" + ":\"" + pad(packager.getFieldPackager(fieldNumb), value) + "\"");
    }

    private String pad(PackagerField packager, String value) {
        return packager.getPadding().pad(value, packager.getLength());
    }

    private String mask(String what, String where) {
        if (what.length() == 19) // probably a PAN
            return where.replace(what, what.substring(0, 6) + getMasking(9) + what.substring(15));

        return where.replace(what, getMasking(what.length()));

    }

    private String getMasking(int length) {
        final char maskingChar = '*';
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(maskingChar);
        }
        return sb.toString();
    }

}
