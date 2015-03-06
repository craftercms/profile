package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.permissions.SubjectResolver;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.utils.SecurityUtils;

/**
 * {@link org.craftercms.commons.security.permissions.SubjectResolver} that returns the currently authenticated
 * profile
 *
 * @author avasquez
 */
public class CurrentUserSubjectResolver implements SubjectResolver<Profile> {

    @Override
    public Profile getCurrentSubject() {
        return SecurityUtils.getCurrentProfile();
    }

}
