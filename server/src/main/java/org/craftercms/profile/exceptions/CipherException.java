package org.craftercms.profile.exceptions;

public class CipherException extends Exception {

	private static final long serialVersionUID = 7551235092424723532L;

	public CipherException(String msg, Throwable e) {	
		super(msg, e);
	}

	public CipherException(Exception e) {
		super(e);
	}
	public CipherException(String msg) {
		super(msg);
	}
}
