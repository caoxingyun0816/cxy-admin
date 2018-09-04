package com.wondertek.mam.util.HJ;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64 {
    public static final String encode(byte[] b) {
        BASE64Encoder encoder = new BASE64Encoder();
        String s = encoder.encode(b);
        s = s.replace('\r', ' ').replace('\n', ' ').replaceAll(" ", "");
        return s;
    }

    public static final byte[] decode(String s) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return decoder.decodeBuffer(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
