package com.cb.oneclipboard.lib.client;

import java.security.MessageDigest;

/**
 * Created by krishnaraj on 22/1/16.
 */
public class Util {

    public static String getSha256Hash(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(text.getBytes("UTF-16LE"));
        byte[] digest = md.digest();

        return bytesToHex(digest);
    }

    // https://stackoverflow.com/a/9855338
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
