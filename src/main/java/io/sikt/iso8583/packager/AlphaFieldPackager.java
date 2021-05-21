//package io.sikt.iso8583.packager;
//
//import io.sikt.iso8583.FieldValue;
//
//public class AlphaFieldPackager<T> implements FieldPackager<T> {
//
//    @Override
//    public byte[] pack(FieldValue<T> fieldValue) {
//        final T value = fieldValue.getValue();
//        if (value instanceof byte[])
//            return ((byte[]) value);
//        else if (value instanceof String)
//            return ((String) value).getBytes(); //TODO:: Charset??
//
//        else
//            throw new RuntimeException("Unsupported value type: " + value.getClass().getSimpleName());
//    }
//
//    @Override
//    public FieldValue<T> unpack(byte[] b) {
//        return null;
//    }
//}
