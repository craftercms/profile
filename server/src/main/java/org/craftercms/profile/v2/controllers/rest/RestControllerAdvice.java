/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
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
package org.craftercms.profile.v2.controllers.rest;

import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.v2.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link org.springframework.web.bind.annotation.ControllerAdvice} for REST controllers that includes exception
 * handling for all controllers.
 *
 * @author avasquez
 */
@ControllerAdvice
public class RestControllerAdvice extends ResponseEntityExceptionHandler {

    private static final I10nLogger logger = new I10nLogger(RestControllerAdvice.class,
            "crafter.profile.messages.logging");

    private static final String LOG_KEY_REST_ERROR = "profile.rest.error";

    @ExceptionHandler(MissingAccessTokenIdParamException.class)
    public ResponseEntity<Object> handleMissingAccessTokenIdParamException(MissingAccessTokenIdParamException e,
                                                                           WebRequest request) {
        return handleExceptionInternal(e, null, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(NoSuchAccessTokenIdException.class)
    public ResponseEntity<Object> handleNoSuchAccessTokenException(NoSuchAccessTokenIdException e, WebRequest request) {
        return handleExceptionInternal(e, null, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(ExpiredAccessTokenException.class)
    public ResponseEntity<Object> handleExpiredAccessTokenException(ExpiredAccessTokenException e, WebRequest request) {
        return handleExceptionInternal(e, null, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(ActionDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(ActionDeniedException e, WebRequest request) {
        return handleExceptionInternal(e, null, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException e, WebRequest request) {
        return handleExceptionInternal(e, null, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(DisabledProfileException.class)
    public ResponseEntity<Object> handleDisabledProfileException(DisabledProfileException e, WebRequest request) {
        return handleExceptionInternal(e, null, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(NoSuchVerificationTokenException.class)
    public ResponseEntity<Object> handleNoSuchVerificationTokenException(NoSuchVerificationTokenException e,
                                                                         WebRequest request) {
        return handleExceptionInternal(e, null, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(ExpiredVerificationTokenException.class)
    public ResponseEntity<Object> handleExpiredVerificationTokenException(ExpiredVerificationTokenException e,
                                                                          WebRequest request) {
        return handleExceptionInternal(e, null, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(InvalidEmailAddressException.class)
    public ResponseEntity<Object> handleInvalidEmailAddressException(InvalidEmailAddressException e,
                                                                     WebRequest request) {
        return handleExceptionInternal(e, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<Object> handlePermissionException(PermissionException e, WebRequest request) {
        return handleExceptionInternal(e, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(ProfileException.class)
    public ResponseEntity<Object> handleProfileException(ProfileException e, WebRequest request) {
        return handleExceptionInternal(e, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        logger.error(LOG_KEY_REST_ERROR, ex, ((ServletWebRequest) request).getRequest().getRequestURI(), status);

        Map<String, Object> error = new HashMap<>(3);
        error.put("code", status.value());
        error.put("message", ex.getLocalizedMessage());

        return new ResponseEntity<Object>(error, headers, status);
    }

}
