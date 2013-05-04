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
package org.craftercms.profile.management.util;

import org.craftercms.profile.management.model.ProfileUserAccountForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ProfileUserAccountValidator implements Validator {
	
	 public boolean supports(Class<?> clazz) {
		 return ProfileUserAccountForm.class.equals(clazz);
	 }

	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "user.validation.error.empty.or.whitespace");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "user.validation.error.empty.or.whitespace");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "user.validation.error.empty.or.whitespace");
	}

}
