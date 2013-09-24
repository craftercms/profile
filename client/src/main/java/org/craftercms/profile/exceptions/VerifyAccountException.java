package org.craftercms.profile.exceptions;

public class VerifyAccountException extends RuntimeException {
	private static final long serialVersionUID = 987920857142577146L;
	public VerifyAccountException() {
        super();
    }

    public VerifyAccountException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public VerifyAccountException(String msg) {
        super(msg);
    }

    public VerifyAccountException(Throwable thr) {
        super(thr);
    }

}
