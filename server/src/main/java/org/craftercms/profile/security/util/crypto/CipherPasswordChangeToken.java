package org.craftercms.profile.security.util.crypto;

import org.craftercms.profile.exceptions.CipherException;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: sokeeffe
 * Date: 9/11/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CipherPasswordChangeToken {
	
	static final String SEP = "|";

    String encrypt(String rawValue) throws CipherException;

    String decrypt(String encryptedValue) throws CipherException;

    void setEncryptionKeyFile(File encryptionKeyFile);

    void init() throws CipherException;

}

