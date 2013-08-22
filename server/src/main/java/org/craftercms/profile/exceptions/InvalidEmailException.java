package org.craftercms.profile.exceptions;

public class InvalidEmailException extends Exception {

	private static final long serialVersionUID = 7551235092424723532L;

	public InvalidEmailException(String msg, Throwable e) {	
		super(msg, e);
	}

	public InvalidEmailException(Exception e) {
		super(e);
	}
	public InvalidEmailException(String msg) {
		super(msg);
	}
}
