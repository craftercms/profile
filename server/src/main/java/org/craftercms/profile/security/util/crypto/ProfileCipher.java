package org.craftercms.profile.security.util.crypto;

import org.craftercms.profile.exceptions.CipherException;

public interface ProfileCipher {
	
	String encrypt(byte[] rawValue) throws CipherException;
	
	String decrypt(String encryptedValue) throws CipherException;
	
	static final String DELIMITER = "|";

}
