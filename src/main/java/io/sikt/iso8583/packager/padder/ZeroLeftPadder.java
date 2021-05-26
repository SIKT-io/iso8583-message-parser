package io.sikt.iso8583.packager.padder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZeroLeftPadder implements Padding {

    private static final char PADDING_CHAR = '0';

    @Override
    public String pad(String what, int maxLength) {
        StringBuilder padded = new StringBuilder(maxLength);
        int length = what.length();
        if (length > maxLength) {
            log.error("Data is longer than maxLength ({} vs {})", length, maxLength);
            return what;
        } else {
            for (int i = maxLength - length; i > 0; i--) {
                padded.append(PADDING_CHAR);
            }
            padded.append(what);
        }
        return padded.toString();
    }

    @Override
    public String unpad(String what) {
        int i = 0;
        int len = what.length();
        while (i < len) {
            if (what.charAt(i) != PADDING_CHAR) {
                return what.substring(i);
            }
            i++;
        }
        return "";
    }
}
