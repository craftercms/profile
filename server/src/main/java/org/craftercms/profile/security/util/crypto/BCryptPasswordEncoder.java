package org.craftercms.profile.security.util.crypto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.authentication.encoding.PasswordEncoder;

public class BCryptPasswordEncoder implements PasswordEncoder {

	protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Hash the supplied password using jBCrypt.
     *
     * @param rawPass <p>the user supplied plain text password</p>
     * @param salt <p>ignored, uses a random salt instead</p>
     * @return the hashed password
     * @throws DataAccessException
     */
    public String encodePassword(String rawPass, Object salt) {
        logger.debug("Encoding password.");
        return BCrypt.hashpw(rawPass, BCrypt.gensalt());
    }

    /**
     * Check the validity of the supplied password.
     *
     * @param encPass <p>the hashed password stored in the database</p>
     * @param rawPass <p>the user supplied plain text password</p>
     * @param salt <p>ignored</p>
     * @return true if the passwords match, false otherwise
     * @throws DataAccessException
     */
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        logger.debug("Validating password.");
        return BCrypt.checkpw(rawPass, encPass);
    }
}
