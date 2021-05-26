package io.sikt.iso8583.packager.padder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpaceRightPadder implements Padding {
    private static final char PADDING_CHAR = ' ';

    @Override
    public String pad(String what, int maxLength) {
        int len = what.length();

        if (len < maxLength) {
            StringBuilder padded = new StringBuilder(maxLength);
            padded.append(what);
            for (; len < maxLength; len++) {
                padded.append(PADDING_CHAR);
            }
            what = padded.toString();
        } else if (len > maxLength) {
            log.error("Data is longer than maxLength ({} vs {})", what.length(), maxLength);
            return pad(what.substring(0, maxLength), maxLength); // trim field and continue
        }
        return what;
    }

    @Override
    public String unpad(String what) {
        int len = what.length();
        for (int i = len; i > 0; i--) {
            if (what.charAt(i - 1) != PADDING_CHAR) {
                return what.substring(0, i);
            }
        }
        return "";
    }
}
