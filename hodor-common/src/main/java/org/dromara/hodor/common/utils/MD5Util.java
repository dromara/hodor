package org.dromara.hodor.common.utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 utils
 *
 * @since 1.0
 */
public class MD5Util {

    public static boolean verify(String src, String code, Charset charset) throws NoSuchAlgorithmException {
        StringBuilder hexValue = encode(src, charset);
        return hexValue.toString().equals(code);
    }

    public static String MD5Encode(String src, Charset charset) throws NoSuchAlgorithmException {
        StringBuilder hexValue = encode(src, charset);
        return hexValue.toString();
    }

    private static StringBuilder encode(String src, Charset charset) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] byteArray = src.getBytes(charset);
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder(32);
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue;
    }
}