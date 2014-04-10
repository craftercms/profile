package org.craftercms.profile.client.exceptions;

/**
 * Thrown whether <code>ProfileClient.forgotPassword</code> or <code>ProfileClient.resetPassword</code> receive a
 * server error.
 *
 * @author Alvaro Gonzalez
 */
public class PasswordException extends RuntimeException {
    private static final long serialVersionUID = 987920857142577145L;

    public PasswordException() {
        super();
    }

    public PasswordException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public PasswordException(String msg) {
        super(msg);
    }

    public PasswordException(Throwable thr) {
        super(thr);
    }
}