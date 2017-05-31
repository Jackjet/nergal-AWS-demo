package com.example.nergal.myaws.awss3v4.signaturev4;

/**
 * Created by nergal on 2017/5/22.
 */



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Various global static functions used.
 */
public class Digest {
    /**
     * Private constructor.
     */
    private Digest() {}


    /**
     * Returns SHA-256 hash of given string.
     */
    public static String sha256Hash(String string) throws NoSuchAlgorithmException {
        return sha256Hash(string.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * Returns SHA-256 hash of given byte array.
     */
    public static String sha256Hash(byte[] data) throws NoSuchAlgorithmException {
        return sha256Hash(data, data.length);
    }


    /**
     * Returns SHA-256 hash string of given byte array and it's length.
     */
    public static String sha256Hash(byte[] data, int length) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        messageDigest.update(data, 0, length);

        return BinaryUtils.toHex(messageDigest.digest()).toLowerCase();
    }


    public static String[] msha256md5Hashes(File file) {
        FileInputStream fis = null;
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length = -1;
            while ((length = fis.read(buffer)) != -1) {
                md5Digest.update(buffer, 0, length);
                sha256Digest.update(buffer, 0, length);
            }
            fis.close();
            System.gc();
            return new String[]{BinaryUtils.toHex(sha256Digest.digest()).toLowerCase(),
                    BinaryUtils.toBase64(md5Digest.digest())};

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fis!=null){
                    fis.close();
                    fis=null;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }



}
