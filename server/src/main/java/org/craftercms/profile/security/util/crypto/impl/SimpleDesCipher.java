package org.craftercms.profile.security.util.crypto.impl;

import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.security.util.crypto.ProfileCipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDesCipher implements ProfileCipher {

    private SecretKey skey;
    private Cipher cipher;
    private final transient Logger log = LoggerFactory.getLogger(SimpleDesCipher.class);

    public SimpleDesCipher(String base64Key) {
        try {
            cipher = Cipher.getInstance("DESede");
        } catch (NoSuchAlgorithmException e1) {
            log.error(e1.getMessage(), e1);
        } catch (NoSuchPaddingException e) {
            log.error(e.getMessage(), e);
        }

        byte[] raw = Base64.decodeBase64(base64Key);

        DESedeKeySpec keyspec;
        try {
            keyspec = new DESedeKeySpec(raw);

            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
            skey = keyfactory.generateSecret(keyspec);
        } catch (InvalidKeyException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        } catch (InvalidKeySpecException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void setKey(byte[] key) throws KeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        DESedeKeySpec keyspec;
        keyspec = new DESedeKeySpec(key);

        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
        skey = keyfactory.generateSecret(keyspec);
    }

    public String encrypt(byte[] clear) throws CipherException {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            byte[] result = cipher.doFinal(clear);
            return Base64.encodeBase64String(result);
        } catch (IllegalBlockSizeException e) {

            throw new CipherException(e);
        } catch (BadPaddingException e) {

            throw new CipherException(e);
        } catch (InvalidKeyException e) {
            throw new CipherException(e);
        }
    }

    public String decrypt(String encrypted) throws CipherException {
        try {
            byte[] data = Base64.decodeBase64(encrypted);
            cipher.init(Cipher.DECRYPT_MODE, skey);
            return new String(cipher.doFinal(data));
        } catch (InvalidKeyException e) {
            throw new CipherException(e);
        } catch (IllegalBlockSizeException e) {
            throw new CipherException(e);
        } catch (BadPaddingException e) {
            throw new CipherException(e);
        }


    }

}
