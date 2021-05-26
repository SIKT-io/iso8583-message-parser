package io.sikt.iso8583.packager.padder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NullPadder implements Padding {

    @Override
    public String pad(String what, int maxLength) {
        return what;
    }

    @Override
    public String unpad(String what) {
        return what;
    }
}
