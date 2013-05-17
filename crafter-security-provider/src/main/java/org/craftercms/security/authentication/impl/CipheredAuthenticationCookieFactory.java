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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.craftercms.security.exception.CrafterSecurityException;
import org.springframework.beans.factory.annotation.Required;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Date;

/**
 * Extends {@link AuthenticationCookieFactory} to decrypt the cookie before creating the object.
 *
 * @author Alfonso Vásquez
 */
public class CipheredAuthenticationCookieFactory extends AuthenticationCookieFactory {

    public static final int ENCRYPTED_VALUE =   0;
    public static final int IV =                1;

    protected Key encryptionKey;

    @Required
    public void setEncryptionKey(String encryptionKey) {
        try {
            this.encryptionKey = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), CipheredAuthenticationCookie.CIPHER_ALGORITHM);
        } catch (UnsupportedEncodingException e) {
            // Shouldn't happen
            throw new CrafterSecurityException(e);
        }
    }

    @Override
    protected AuthenticationCookie createCookie(String ticket, Date profileOutdatedAfter) {
        return new CipheredAuthenticationCookie(ticket, profileOutdatedAfter, encryptionKey);
    }

    @Override
    protected String getCookieValueFromRequest(HttpServletRequest request) {
        String cookieValue = super.getCookieValueFromRequest(request);
        if (cookieValue != null) {
            String[] encryptedValueAndIv = StringUtils.split(cookieValue, AuthenticationCookie.COOKIE_SEP);

            return decrypt(encryptedValueAndIv[ENCRYPTED_VALUE], Base64.decodeBase64(encryptedValueAndIv[IV]));
        } else {
            return null;
        }
    }

    protected String decrypt(String encryptedValue, byte[] iv) throws CrafterSecurityException {
        try {
            byte[] encryptedBytes = Base64.decodeBase64(encryptedValue);

            Cipher cipher = Cipher.getInstance(CipheredAuthenticationCookie.CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new IvParameterSpec(iv));

            byte[] decryptedValue = cipher.doFinal(encryptedBytes);

            return new String(decryptedValue, "UTF-8");
        } catch (Exception e) {
            throw new CrafterSecurityException("Error while trying to decrypt cookie value", e);
        }
    }

}
