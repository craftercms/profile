package org.craftercms.profile.services;

/**
 * Validates if a value has the email format expected.
 * @author Alvaro Gonzalez
 *
 */
public interface EmailValidatorService {

	/**
	 * Gets is the email passed as parameter has the email expected format
	 * 
	 * @param email is going to be validate
	 * 
	 * @return <code>true</code> if the email has the format expected, <code>false</code> otherwise
	 */
    public boolean validateEmail(String email);

}
