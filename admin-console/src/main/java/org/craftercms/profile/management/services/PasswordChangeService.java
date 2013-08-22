package org.craftercms.profile.management.services;

import org.craftercms.profile.exceptions.AppAuthenticationFailedException;

public interface PasswordChangeService {
	
//	void forgotPassword(String tenantName, String username, String changePasswordUrl) throws AppAuthenticationFailedException;
	void changePassword(String token, String newPassword) throws AppAuthenticationFailedException;
	void forgotPassword(String tenantName, String username) throws AppAuthenticationFailedException;

}
