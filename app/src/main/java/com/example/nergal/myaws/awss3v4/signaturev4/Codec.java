package com.example.nergal.myaws.awss3v4.signaturev4;

/**
 * Created by nergal on 2017/5/22.
 */

interface Codec {
    byte[] encode(byte[] var1);

    byte[] decode(byte[] var1, int var2);
}
