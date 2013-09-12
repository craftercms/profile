package org.craftercms.profile.security.util.crypto.impl;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.security.util.KeyFile;
import org.craftercms.profile.security.util.crypto.CipherPasswordChangeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CipherPasswordChangeTokenImpl implements CipherPasswordChangeToken {

    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final char SEP = '|';
    private Key encryptionKey;
    private static final SecureRandom secureRandom = new SecureRandom();
    protected final Log logger = LogFactory.getLog(getClass());

    protected File encryptionKeyFile;
    public static final String CIPHER_ALGORITHM = "AES";
    public static final int ENCRYPTED_VALUE = 0;
    public static final int IV = 1;


    @Override
    public String encrypt(String rawValue) throws CipherException {
        try {
            byte[] iv = generateIv();

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, new IvParameterSpec(iv));

            byte[] encryptedValue = cipher.doFinal(rawValue.getBytes("UTF-8"));

            // The IV can be sent in clear text, no security issue on that
            return Base64.encodeBase64String(encryptedValue) + SEP + Base64.encodeBase64String(iv);
        } catch (Exception e) {
            throw new CipherException("Error while trying to encrypt token value " + rawValue, e);
        }
    }

    @Override
    public String decrypt(String encryptedValue) throws CipherException {
        String[] encryptedValueAndIv = StringUtils.split(encryptedValue, SEP);

        return decrypt(encryptedValueAndIv[ENCRYPTED_VALUE], Base64.decodeBase64(encryptedValueAndIv[IV]));
    }


    private String decrypt(String encryptedValue, byte[] iv) throws CipherException {
        try {
            byte[] encryptedBytes = Base64.decodeBase64(encryptedValue);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new IvParameterSpec(iv));

            byte[] decryptedValue = cipher.doFinal(encryptedBytes);

            return new String(decryptedValue, "UTF-8");
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

    /**
     * Sets the file to the token encryption key.
     */
    /*@Required*/
    @Override
    public void setEncryptionKeyFile(File encryptionKeyFile) {
        this.encryptionKeyFile = encryptionKeyFile;
    }

    /**
     * Tries to read the encryption key from the file. If the file doesn't exist or is empty,
     * a new encryption key is randomly generated
     * and stored in the file, so that it can be used after reboots.
     */
    @PostConstruct
    public void init() throws CipherException {
        KeyFile keyFile = getEncryptionKeyFile();

        if (encryptionKeyFile.length() > 0) {
            try {
                encryptionKey = keyFile.readKey();
            } catch (IOException e) {
                throw new CipherException("Error while trying to read encryption key from file " + encryptionKeyFile,
                    e);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Found encryption key for authentication tokens in file " + encryptionKeyFile);
            }
        } else {
            encryptionKey = generateRandomKey();
            try {
                keyFile.writeKey(encryptionKey);
            } catch (IOException e) {
                throw new CipherException("Error while trying to write encryption key to file " + encryptionKeyFile, e);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("No encryption key for authentication tokens found. A new random encryption key was " +
                    "generated and " +
                    "stored in file " + encryptionKeyFile + " for future use");
            }
        }
    }

    /**
     * Generates a random encryption key.
     */
    protected Key generateRandomKey() throws CipherException {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(CIPHER_ALGORITHM);
            keyGenerator.init(secureRandom);

            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new CipherException("Unable to generate random encryption key", e);
        }
    }

    /**
     * Returns the encryption key file.
     */
    protected KeyFile getEncryptionKeyFile() {
        return new KeyFile(encryptionKeyFile);
    }

    @Value("${crafter.profile.password.change.token.encryptionKey.file}")
    public void setEncryptionKeyFile(String encryptionKeyFile) {
        this.encryptionKeyFile = new File(encryptionKeyFile);
    }


}
