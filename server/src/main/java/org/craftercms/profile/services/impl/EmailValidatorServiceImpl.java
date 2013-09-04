package org.craftercms.profile.services.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.craftercms.profile.services.EmailValidatorService;
import org.springframework.stereotype.Service;

@Service
public class EmailValidatorServiceImpl implements EmailValidatorService {

    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\" +
        ".[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public EmailValidatorServiceImpl() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    /**
     * Validate email with regular expression
     *
     * @param email email for validation
     * @return true valid email, false invalid email
     */
    public boolean validateEmail(final String email) {

        matcher = pattern.matcher(email);
        return matcher.matches();

    }
}
