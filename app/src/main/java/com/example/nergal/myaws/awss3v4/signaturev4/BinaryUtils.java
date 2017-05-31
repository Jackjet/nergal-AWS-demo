package com.example.nergal.myaws.awss3v4.signaturev4;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by nergal on 2017/5/22.
 */

public class BinaryUtils {

    public BinaryUtils() {
    }

    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);

        for(int i = 0; i < data.length; ++i) {
            String hex = Integer.toHexString(data[i]);
            if(hex.length() == 1) {
                sb.append("0");
            } else if(hex.length() == 8) {
                hex = hex.substring(6);
            }

            sb.append(hex);
        }

        return lowerCase(sb.toString());
    }
    public static String lowerCase(String str) {
        return str == null?null:(str.isEmpty()?"":str.toLowerCase(Locale.ENGLISH));
    }



    public static String toBase64(byte[] data) {
        return Base64.encodeAsString(data);
    }


}