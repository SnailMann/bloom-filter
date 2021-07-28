package com.snailmann.bloom.utils;

/**
 * @author liwenjie
 */
public class ByteUtils {

    private static final int B = 8;

    public static int bitCount(byte[] bytes) {
        int count = 0;
        for (byte b : bytes) {
            byte a = b;
            while (a != 0) {
                a &= (a - 1);
                count++;
            }
        }
        return count;
    }
}
