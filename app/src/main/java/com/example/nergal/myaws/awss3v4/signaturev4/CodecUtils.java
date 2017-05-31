package com.example.nergal.myaws.awss3v4.signaturev4;

/**
 * Created by nergal on 2017/5/22.
 */

public class CodecUtils {

    private CodecUtils() {
    }



    public static byte[] toBytesDirect(String singleOctets) {
        char[] src = singleOctets.toCharArray();
        byte[] dest = new byte[src.length];

        for(int i = 0; i < dest.length; ++i) {
            char c = src[i];
            if(c > 127) {
                throw new IllegalArgumentException("Invalid character found at position " + i + " for " + singleOctets);
            }

            dest[i] = (byte)c;
        }

        return dest;
    }

    public static String toStringDirect(byte[] bytes) {
        char[] dest = new char[bytes.length];
        int i = 0;
        byte[] var3 = bytes;
        int var4 = bytes.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte b = var3[var5];
            dest[i++] = (char)b;
        }

        return new String(dest);
    }

    static void sanityCheckLastPos(int pos, int mask) {
        if((pos & mask) != 0) {
            throw new IllegalArgumentException("Invalid last non-pad character detected");
        }
    }
}