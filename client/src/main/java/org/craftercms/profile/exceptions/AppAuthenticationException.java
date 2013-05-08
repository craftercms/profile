package org.craftercms.profile.exceptions;

public class AppAuthenticationException extends RuntimeException {
	
	public AppAuthenticationException() {
		super();
	}

	public AppAuthenticationException(String msg, Throwable thr) {
		super(msg, thr);
	}

	public AppAuthenticationException(String msg) {
		super(msg);
	}

	public AppAuthenticationException(Throwable thr) {
		super(thr);
	}

}
