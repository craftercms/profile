package org.craftercms.profile.exceptions;

public class ResourceExistException extends RuntimeException {
	
	public ResourceExistException() {
		super();
	}

	public ResourceExistException(String msg, Throwable thr) {
		super(msg, thr);
	}

	public ResourceExistException(String msg) {
		super(msg);
	}

	public ResourceExistException(Throwable thr) {
		super(thr);
	}

}
