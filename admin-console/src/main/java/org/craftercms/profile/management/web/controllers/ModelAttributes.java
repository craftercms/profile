package org.craftercms.profile.management.web.controllers;

import org.craftercms.profile.api.Profile;
import org.craftercms.security.utils.SecurityUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * {@link org.springframework.web.bind.annotation.ControllerAdvice} that defines common model attributes for all
 * view controllers.
 *
 * @author avasquez
 */
@ControllerAdvice
public class ModelAttributes {

    public static final String MODEL_LOGGED_IN_USER = "loggedInUser";

    @ModelAttribute(MODEL_LOGGED_IN_USER)
    public Profile getLoggedInUser() {
        return SecurityUtils.getCurrentProfile();
    }

}
