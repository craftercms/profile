package org.craftercms.profile.management.util;

import org.craftercms.profile.impl.domain.GroupRole;
import org.craftercms.profile.impl.domain.Tenant;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
@Component
public class GroupRoleValidator implements Validator{
    @Override
    public boolean supports(Class<?> aClass) {
        return GroupRole.class.equals(aClass);
    }

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "grouprole.mapping.name.validation.error.empty");
		
	}

}
