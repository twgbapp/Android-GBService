package goldenbrother.gbmobile.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by asus on 2016/5/5.
 */

public class EncryptHelper {

    public static String md5(String text) {
        return getMessageDigest("MD5", text);
    }

    public static String sha256(String text) {
        return getMessageDigest("SHA-256", text);
    }

    public static String sha512(String text) {
        return getMessageDigest("SHA-512", text);
    }

    private static String getMessageDigest(String algorithm, String text) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(text.getBytes());
            byte[] bArr = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bArr) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}