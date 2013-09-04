package org.craftercms.profile.security.util.crypto.impl;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.security.util.crypto.ProfileCipher;


public class AESCipher implements ProfileCipher {

    public static final SecureRandom secureRandom = new SecureRandom();

    private String token;
    private Date expirationDate;

    protected byte[] encryptionKey;


    //	public static final String CIPHER_ALGORITHM =       "AES";
    public static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";


    public AESCipher(String token, Date expirationDate, byte[] encryptionKey) {
        this.token = token;
        this.expirationDate = expirationDate;


        this.encryptionKey = encryptionKey;
    }


    public String toTokenValue() throws CipherException {
        return encrypt(getTokenValue().getBytes());
    }

    /**
     * Encrypts the cookie values using the AES cipher and returns it as a Base 64 string.
     *
     * @throws CipherException
     */
    public String encrypt(byte[] rawValue) throws CipherException {
        try {
            byte[] iv = generateIv();

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKey, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

            byte[] encryptedValue = cipher.doFinal(rawValue);

            // The IV can be sent in clear text, no security issue on that
            String encrypted = Base64.encodeBase64String(encryptedValue) + DELIMITER + Base64.encodeBase64String(iv);
            return encrypted;
        } catch (Exception e) {
            throw new CipherException("Error while trying to encrypt token value " + rawValue, e);
        }
    }

    public String decrypt(String encryptedValue) throws CipherException {
        try {
            byte[] encryptedBytes = Base64.decodeBase64(encryptedValue);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKey, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            //cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new IvParameterSpec(iv));


            byte[] decryptedValue = cipher.doFinal(encryptedBytes);

            return new String(decryptedValue);
        } catch (Exception e) {
            throw new CipherException("Error while trying to decrypt token value", e);
        }
    }

    /**
     * Generates an initialization vector for the cipher.
     */
    protected byte[] generateIv() {
        byte[] iv = new byte[16];

        secureRandom.nextBytes(iv);

        return iv;
    }

    @Override
    public String toString() {
        return "CiphereToken[" +
            "token='" + token + '\'' +
            ", expirationDate=" + this.expirationDate +
            ']';
    }

    private String getTokenValue() {
        return token + DELIMITER + DateFormat.getDateTimeInstance().format(this.expirationDate);
    }


}
