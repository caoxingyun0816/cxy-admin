package com.wondertek.mam.util;

public class IndexingException extends Exception {
	private static final long serialVersionUID = -2487077348507690277L;

	public IndexingException() {
    }

    public IndexingException(String message) {
        super(message);
    }

    public IndexingException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndexingException(Throwable cause) {
        super(cause);
    }

    public IndexingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
