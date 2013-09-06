package org.craftercms.profile.exceptions;

public class ExpiryDateException extends Exception {

    private static final long serialVersionUID = 7551235092424723532L;

    public ExpiryDateException(String msg, Throwable e) {
        super(msg, e);
    }

    public ExpiryDateException(Exception e) {
        super(e);
    }

    public ExpiryDateException(String msg) {
        super(msg);
    }
}