package org.springultron.core.utils;

import org.springultron.core.exception.CryptoException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 摘要算法工具类
 *
 * @author brucewuu
 * @date 2019-06-06 10:44
 */
public class DigestUtils {

    private DigestUtils() {
    }

    private static final int STREAM_BUFFER_LENGTH = 1024;

    public static byte[] md5(byte[] valueToDigest) {
        return digest("MD5", valueToDigest);
    }

    public static byte[] md5(String valueToDigest) {
        return digest("MD5", valueToDigest.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] md5(InputStream valueToDigest) throws IOException {
        return digest("MD5", valueToDigest);
    }

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex string.
     *
     * @param valueToDigest Data to digest
     * @return MD5 digest as a hex string
     */
    public static String md5Hex(final String valueToDigest) {
        byte[] bytes = md5(valueToDigest);
        return Hex.encodeHexString(bytes);
    }

    public static String sha1Hex(final String valueToDigest) {
        return hashHex("SHA-1", valueToDigest);
    }

    public static String sha256Hex(final String valueToDigest) {
        return hashHex("SHA-256", valueToDigest);
    }

    public static String sha384Hex(final String valueToDigest) {
        return hashHex("SHA-384", valueToDigest);
    }

    public static String sha512Hex(final String valueToDigest) {
        return hashHex("SHA-512", valueToDigest);
    }

    /**
     * hash摘要算法
     *
     * @param algorithm     算法
     * @param valueToDigest data
     * @return digest as a hex string
     */
    public static String hashHex(final String algorithm, final String valueToDigest) {
        byte[] bytes = digest(algorithm, valueToDigest.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(bytes);
    }

    /**
     * 摘要算法
     *
     * @param algorithm     算法
     * @param valueToDigest data
     * @return digest as byte array
     */
    public static byte[] digest(final String algorithm, final byte[] valueToDigest) {
        return getDigest(algorithm).digest(valueToDigest);
    }

    /**
     * 摘要算法
     *
     * @param algorithm     算法
     * @param valueToDigest data输入流
     * @return digest as byte array
     */
    public static byte[] digest(final String algorithm, final InputStream valueToDigest) {
        MessageDigest messageDigest = getDigest(algorithm);
        final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read;
        try {
            while ((read = valueToDigest.read(buffer, 0, STREAM_BUFFER_LENGTH)) > -1) {
                messageDigest.update(buffer, 0, read);
            }
            return messageDigest.digest();
        } catch (final IOException e) {
            throw new CryptoException(e);
        }
    }

    public static MessageDigest getDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new CryptoException("Could not find MessageDigest with algorithm \"" + algorithm + "\"", e);
        }
    }

    public static byte[] hmacMd5(final byte[] valueToDigest, final String key) {
        return hmac("HmacMD5", valueToDigest, key);
    }

    public static String hmacMd5Hex(final byte[] valueToDigest, final String key) {
        return hmacHex("HmacMD5", valueToDigest, key);
    }

    public static byte[] hmacMd5(final String valueToDigest, final String key) {
        return hmac("HmacMD5", valueToDigest, key);
    }

    public static String hmacMd5Hex(final String valueToDigest, final String key) {
        return hmacHex("HmacMD5", valueToDigest, key);
    }

    public static byte[] hmacSha1(final byte[] valueToDigest, final String key) {
        return hmac("HmacSHA1", valueToDigest, key);
    }

    public static String hmacSha1Hex(final byte[] valueToDigest, final String key) {
        return hmacHex("HmacSHA1", valueToDigest, key);
    }

    public static byte[] hmacSha1(final String valueToDigest, final String key) {
        return hmac("HmacSHA1", valueToDigest, key);
    }

    public static String hmacSha1Hex(final String valueToDigest, final String key) {
        return hmacHex("HmacSHA1", valueToDigest, key);
    }

    public static byte[] hmacSha256(final byte[] valueToDigest, final String key) {
        return hmac("HmacSHA256", valueToDigest, key);
    }

    public static String hmacSha256Hex(final byte[] valueToDigest, final String key) {
        return hmacHex("HmacSHA256", valueToDigest, key);
    }

    public static byte[] hmacSha256(final String valueToDigest, final String key) {
        return hmac("HmacSHA256", valueToDigest, key);
    }

    public static String hmacSha256Hex(final String valueToDigest, final String key) {
        return hmacHex("HmacSHA256", valueToDigest, key);
    }

    public static byte[] hmacSha384(final byte[] valueToDigest, final String key) {
        return hmac("HmacSHA384", valueToDigest, key);
    }

    public static String hmacSha384Hex(final byte[] valueToDigest, final String key) {
        return hmacHex("HmacSHA384", valueToDigest, key);
    }

    public static byte[] hmacSha384(final String valueToDigest, final String key) {
        return hmac("HmacSHA384", valueToDigest, key);
    }

    public static String hmacSha384Hex(final String valueToDigest, final String key) {
        return hmacHex("HmacSHA384", valueToDigest, key);
    }

    public static byte[] hmacSha512(final byte[] valueToDigest, final String key) {
        return hmac("HmacSHA512", valueToDigest, key);
    }

    public static String hmacSha512Hex(final byte[] valueToDigest, final String key) {
        return hmacHex("HmacSHA512", valueToDigest, key);
    }

    public static byte[] hmacSha512(final String valueToDigest, final String key) {
        return hmac("HmacSHA512", valueToDigest, key);
    }

    public static String hmacSha512Hex(final String valueToDigest, final String key) {
        return hmacHex("HmacSHA512", valueToDigest, key);
    }

    public static byte[] hmac(final String algorithm, final byte[] valueToDigest, final String key) {
        return getInitializedMac(algorithm, key).doFinal(valueToDigest);
    }

    public static String hmacHex(final String algorithm, final byte[] valueToDigest, final String key) {
        return Hex.encodeHexString(hmac(algorithm, valueToDigest, key));
    }

    public static byte[] hmac(final String algorithm, final String valueToDigest, final String key) {
        return getInitializedMac(algorithm, key).doFinal(valueToDigest.getBytes(StandardCharsets.UTF_8));
    }

    public static String hmacHex(final String algorithm, final String valueToDigest, final String key) {
        return Hex.encodeHexString(hmac(algorithm, valueToDigest, key));
    }

    public static byte[] hmac(final String algorithm, final InputStream valueToDigest, final String key) throws IOException {
        final Mac mac = getInitializedMac(algorithm, key);
        final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read;
        while ((read = valueToDigest.read(buffer, 0, STREAM_BUFFER_LENGTH)) > -1) {
            mac.update(buffer, 0, read);
        }
        return mac.doFinal();
    }

    public static String hmacHex(final String algorithm, final InputStream valueToDigest, final String key) throws IOException {
        return Hex.encodeHexString(hmac(algorithm, valueToDigest, key));
    }

    public static byte[] hmac(final String algorithm, final ByteBuffer valueToDigest, final String key) {
        final Mac mac = getInitializedMac(algorithm, key);
        mac.update(valueToDigest);
        return mac.doFinal();
    }

    public static String hmacHex(final String algorithm, final ByteBuffer valueToDigest, final String key) {
        return Hex.encodeHexString(hmac(algorithm, valueToDigest, key));
    }

    public byte[] hmac(final String algorithm, final File valueToDigest, final String key) throws IOException {
        try (final BufferedInputStream stream = new BufferedInputStream(new FileInputStream(valueToDigest))) {
            return hmac(algorithm, stream, key);
        }
    }

    public String hmacHex(final String algorithm, final File valueToDigest, final String key) throws IOException {
        try (final BufferedInputStream stream = new BufferedInputStream(new FileInputStream(valueToDigest))) {
            return hmacHex(algorithm, stream, key);
        }
    }

    public static Mac getInitializedMac(final String algorithm, final byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Null key");
        }
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
            final Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);
            return mac;
        } catch (final NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CryptoException(e);
        }
    }

    public static Mac getInitializedMac(final String algorithm, final String key) {
        if (key == null) {
            throw new CryptoException("Null key");
        }
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
            final Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);
            return mac;
        } catch (final NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CryptoException(e);
        }
    }

}
