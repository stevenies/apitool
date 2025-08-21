package com.smn.restapigenerator.exception;

public class ExceptionAccessTokenInUse extends Exception {

    public ExceptionAccessTokenInUse() {
        super("The access token is already in use by another user.");
    }

    public ExceptionAccessTokenInUse(String message) {
        super(message);
    }

    public ExceptionAccessTokenInUse(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionAccessTokenInUse(Throwable cause) {
        super(cause);
    }

}
