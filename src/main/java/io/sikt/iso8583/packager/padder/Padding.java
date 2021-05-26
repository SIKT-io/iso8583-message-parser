package io.sikt.iso8583.packager.padder;

public interface Padding {

    String pad(String what, int maxLength);

    String unpad(String what);

}
