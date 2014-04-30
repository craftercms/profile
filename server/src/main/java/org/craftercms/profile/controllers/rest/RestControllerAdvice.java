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
package org.craftercms.profile.controllers.rest;

import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.profile.api.exceptions.ErrorCode;
import org.craftercms.profile.api.exceptions.ErrorDetails;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
        return handleExceptionInternal(e, HttpStatus.UNAUTHORIZED, ErrorCode.MISSING_ACCESS_TOKEN_ID_PARAM, request);
    }

    @ExceptionHandler(NoSuchAccessTokenIdException.class)
    public ResponseEntity<Object> handleNoSuchAccessTokenException(NoSuchAccessTokenIdException e, WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.FORBIDDEN, ErrorCode.NO_SUCH_ACCESS_TOKEN_ID, request);
    }

    @ExceptionHandler(ExpiredAccessTokenException.class)
    public ResponseEntity<Object> handleExpiredAccessTokenException(ExpiredAccessTokenException e, WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.FORBIDDEN, ErrorCode.EXPIRED_ACCESS_TOKEN, request);
    }

    @ExceptionHandler(ActionDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(ActionDeniedException e, WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.FORBIDDEN, ErrorCode.ACTION_DENIED, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException e, WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, request);
    }

    @ExceptionHandler(DisabledProfileException.class)
    public ResponseEntity<Object> handleDisabledProfileException(DisabledProfileException e, WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.FORBIDDEN, ErrorCode.DISABLED_PROFILE, request);
    }

    @ExceptionHandler(NoSuchTicketException.class)
    public ResponseEntity<Object> handleNoSuchTicketException(NoSuchTicketException e, WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.BAD_REQUEST, ErrorCode.NO_SUCH_TICKET, request);
    }

    @ExceptionHandler(NoSuchVerificationTokenException.class)
    public ResponseEntity<Object> handleNoSuchVerificationTokenException(NoSuchVerificationTokenException e,
                                                                         WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.FORBIDDEN, ErrorCode.NO_SUCH_VERIFICATION_TOKEN, request);
    }

    @ExceptionHandler(ExpiredVerificationTokenException.class)
    public ResponseEntity<Object> handleExpiredVerificationTokenException(ExpiredVerificationTokenException e,
                                                                          WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.FORBIDDEN, ErrorCode.EXPIRED_VERIFICATION_TOKEN, request);
    }

    @ExceptionHandler(InvalidEmailAddressException.class)
    public ResponseEntity<Object> handleInvalidEmailAddressException(InvalidEmailAddressException e,
                                                                     WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_EMAIL_ADDRESS, request);
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<Object> handlePermissionException(PermissionException e, WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.PERMISSION_ERROR, request);
    }

    @ExceptionHandler(AttributeAlreadyDefinedException.class)
    public ResponseEntity<Object> handleAttributeAlreadyDefinedException(AttributeAlreadyDefinedException e,
                                                                         WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.BAD_REQUEST, ErrorCode.ATTRIBUTE_ALREADY_DEFINED, request);
    }

    @ExceptionHandler(AttributeNotDefinedException.class)
    public ResponseEntity<Object> handleAttributeNotDefinedDefinedException(AttributeNotDefinedException e,
                                                                            WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.BAD_REQUEST, ErrorCode.ATTRIBUTE_NOT_DEFINED, request);
    }

    @ExceptionHandler(AttributeDefinitionStillUsedException.class)
    public ResponseEntity<Object> handleAttributeDefinitionStillUsedException(AttributeDefinitionStillUsedException e,
                                                                              WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.FORBIDDEN, ErrorCode.ATTRIBUTE_DEFINITION_STILL_USED, request);
    }

    @ExceptionHandler(TenantExistsException.class)
    public ResponseEntity<Object> handleTenantExistsException(TenantExistsException e, WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.BAD_REQUEST, ErrorCode.TENANT_EXISTS, request);
    }

    @ExceptionHandler(ProfileException.class)
    public ResponseEntity<Object> handleProfileException(ProfileException e, WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, headers, status, ErrorCode.GENERAL_ERROR, request);
    }

    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, HttpStatus status,
                                                             ErrorCode errorCode,
                                                             WebRequest request) {
        return handleExceptionInternal(ex, new HttpHeaders(), status, errorCode, request);
    }

    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, HttpHeaders headers, HttpStatus status,
                                                             ErrorCode errorCode, WebRequest request) {
        logger.error(LOG_KEY_REST_ERROR, ex, ((ServletWebRequest) request).getRequest().getRequestURI(), status);

        return new ResponseEntity<Object>(new ErrorDetails(errorCode, ex.getLocalizedMessage()), headers, status);
    }

}
