package org.craftercms.profile.loader.exceptions;

/**
 * 
 * @author Sandra O'Keeffe
 */
public class ProfileLoadException extends Exception {

	private static final long serialVersionUID = 1L;

	public ProfileLoadException(String msg, Throwable e) {
		super(msg, e);
	}

	public ProfileLoadException(Exception e) {
		super(e);
	}
 
}
