package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.permissions.SubjectResolver;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.utils.SecurityUtils;

/**
 * Created by alfonsovasquez on 20/2/15.
 */
public class CurrentUserSubjectResolver implements SubjectResolver<Profile> {

    @Override
    public Profile getCurrentSubject() {
        return SecurityUtils.getCurrentProfile();
    }

}
