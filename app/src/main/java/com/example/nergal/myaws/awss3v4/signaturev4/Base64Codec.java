package com.example.nergal.myaws.awss3v4.signaturev4;

/**
 * Created by nergal on 2017/5/22.
 */



class Base64Codec implements Codec {

    private final byte[] alpahbets;

    Base64Codec() {
        this.alpahbets = CodecUtils.toBytesDirect("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/");
    }


    public byte[] encode(byte[] src) {
        int num3bytes = src.length / 3;
        int remainder = src.length % 3;
        byte[] dest;
        int s;
        int d;
        if(remainder == 0) {
            dest = new byte[num3bytes * 4];
            s = 0;

            for(d = 0; s < src.length; d += 4) {
                this.encode3bytes(src, s, dest, d);
                s += 3;
            }

            return dest;
        } else {
            dest = new byte[(num3bytes + 1) * 4];
            s = 0;

            for(d = 0; s < src.length - remainder; d += 4) {
                this.encode3bytes(src, s, dest, d);
                s += 3;
            }

            switch(remainder) {
                case 1:
                    this.encode1byte(src, s, dest, d);
                    break;
                case 2:
                    this.encode2bytes(src, s, dest, d);
            }

            return dest;
        }
    }

    void encode3bytes(byte[] src, int s, byte[] dest, int d) {
        byte p;
        dest[d++] = this.alpahbets[(p = src[s++]) >>> 2 & 63];
        dest[d++] = this.alpahbets[(p & 3) << 4 | (p = src[s++]) >>> 4 & 15];
        dest[d++] = this.alpahbets[(p & 15) << 2 | (p = src[s]) >>> 6 & 3];
        dest[d] = this.alpahbets[p & 63];
    }

    void encode2bytes(byte[] src, int s, byte[] dest, int d) {
        byte p;
        dest[d++] = this.alpahbets[(p = src[s++]) >>> 2 & 63];
        dest[d++] = this.alpahbets[(p & 3) << 4 | (p = src[s]) >>> 4 & 15];
        dest[d++] = this.alpahbets[(p & 15) << 2];
        dest[d] = 61;
    }

    void encode1byte(byte[] src, int s, byte[] dest, int d) {
        byte p;
        dest[d++] = this.alpahbets[(p = src[s]) >>> 2 & 63];
        dest[d++] = this.alpahbets[(p & 3) << 4];
        dest[d++] = 61;
        dest[d] = 61;
    }

    void decode4bytes(byte[] src, int s, byte[] dest, int d) {
        boolean p = false;
        int var6;
        dest[d++] = (byte)(this.pos(src[s++]) << 2 | (var6 = this.pos(src[s++])) >>> 4 & 3);
        dest[d++] = (byte)((var6 & 15) << 4 | (var6 = this.pos(src[s++])) >>> 2 & 15);
        dest[d] = (byte)((var6 & 3) << 6 | this.pos(src[s]));
    }

    void decode1to3bytes(int n, byte[] src, int s, byte[] dest, int d) {
        boolean p = false;
        int var7;
        dest[d++] = (byte)(this.pos(src[s++]) << 2 | (var7 = this.pos(src[s++])) >>> 4 & 3);
        if(n == 1) {
            CodecUtils.sanityCheckLastPos(var7, 15);
        } else {
            dest[d++] = (byte)((var7 & 15) << 4 | (var7 = this.pos(src[s++])) >>> 2 & 15);
            if(n == 2) {
                CodecUtils.sanityCheckLastPos(var7, 3);
            } else {
                dest[d] = (byte)((var7 & 3) << 6 | this.pos(src[s]));
            }
        }
    }

    public byte[] decode(byte[] src, int length) {
        if(length % 4 != 0) {
            throw new IllegalArgumentException("Input is expected to be encoded in multiple of 4 bytes but found: " + length);
        } else {
            int pads = 0;

            for(int last = length - 1; pads < 2 && last > -1 && src[last] == 61; ++pads) {
                --last;
            }

            byte fq;
            switch(pads) {
                case 0:
                    fq = 3;
                    break;
                case 1:
                    fq = 2;
                    break;
                case 2:
                    fq = 1;
                    break;
                default:
                    throw new Error("Impossible");
            }

            byte[] dest = new byte[length / 4 * 3 - (3 - fq)];
            int s = 0;

            int d;
            for(d = 0; d < dest.length - fq % 3; d += 3) {
                this.decode4bytes(src, s, dest, d);
                s += 4;
            }

            if(fq < 3) {
                this.decode1to3bytes(fq, src, s, dest, d);
            }

            return dest;
        }
    }

    protected int pos(byte in) {
        byte pos = Base64Codec.LazyHolder.DECODED[in];
        if(pos > -1) {
            return pos;
        } else {
            throw new IllegalArgumentException("Invalid base 64 character: \'" + (char)in + "\'");
        }
    }

    private static class LazyHolder {
        private static final byte[] DECODED = decodeTable();

        private LazyHolder() {
        }

        private static byte[] decodeTable() {
            byte[] dest = new byte[123];

            for(int i = 0; i <= 122; ++i) {
                if(i >= 65 && i <= 90) {
                    dest[i] = (byte)(i - 65);
                } else if(i >= 48 && i <= 57) {
                    dest[i] = (byte)(i - -4);
                } else if(i == 43) {
                    dest[i] = (byte)(i - -19);
                } else if(i == 47) {
                    dest[i] = (byte)(i - -16);
                } else if(i >= 97 && i <= 122) {
                    dest[i] = (byte)(i - 71);
                } else {
                    dest[i] = -1;
                }
            }

            return dest;
        }
    }
}

