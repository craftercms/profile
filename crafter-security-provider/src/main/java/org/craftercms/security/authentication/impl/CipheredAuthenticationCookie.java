/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.security.authentication.impl;

import java.security.Key;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.craftercms.security.exception.CrafterSecurityException;

/**
 * Extends {@link AuthenticationCookie} to encrypt the cookie before saving (using AES algorithm).
 *
 * @author Alfonso Vásquez
 */
public class CipheredAuthenticationCookie extends AuthenticationCookie {

    protected Key encryptionKey;

    public CipheredAuthenticationCookie(String ticket, Date profileOutdatedAfter, Key encryptionKey) {
        super(ticket, profileOutdatedAfter);

        this.encryptionKey = encryptionKey;
    }

    @Override
    public String toCookieValue() {
        return encrypt(super.toCookieValue());
    }

    /**
     * Encrypts the cookie values using the AES cipher and returns it as a Base 64 string.
     */
    protected String encrypt(String rawValue) throws CrafterSecurityException {
        try {
            byte[] iv = generateIv();

            Cipher cipher = Cipher.getInstance(CipheredAuthenticationCookieFactory.CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, new IvParameterSpec(iv));

            byte[] encryptedValue = cipher.doFinal(rawValue.getBytes("UTF-8"));

            // The IV can be sent in clear text, no security issue on that
            return Base64.encodeBase64String(encryptedValue) + COOKIE_SEP + Base64.encodeBase64String(iv);
        } catch (Exception e) {
            throw new CrafterSecurityException("Error while trying to encrypt cookie value " + rawValue, e);
        }
    }

    /**
     * Generates an initialization vector for the cipher.
     */
    protected byte[] generateIv() {
        byte[] iv = new byte[16];

        CipheredAuthenticationCookieFactory.secureRandom.nextBytes(iv);

        return iv;
    }

    @Override
    public String toString() {
        return "CipheredAuthenticationCookie[" +
            "ticket='" + ticket + '\'' +
            ", profileOutdatedAfter=" + profileOutdatedAfter +
            ']';
    }

}
