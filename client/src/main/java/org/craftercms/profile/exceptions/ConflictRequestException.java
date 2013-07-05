package org.craftercms.profile.exceptions;

public class ConflictRequestException extends RuntimeException {
	
	public ConflictRequestException() {
		super();
	}

	public ConflictRequestException(String msg, Throwable thr) {
		super(msg, thr);
	}

	public ConflictRequestException(String msg) {
		super(msg);
	}

	public ConflictRequestException(Throwable thr) {
		super(thr);
	}

}