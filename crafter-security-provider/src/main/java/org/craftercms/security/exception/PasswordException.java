package org.craftercms.security.exception;

public class PasswordException extends RuntimeException {
    private static final long serialVersionUID = 987920857142577147L;

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
