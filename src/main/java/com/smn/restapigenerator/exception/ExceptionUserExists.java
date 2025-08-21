package com.smn.restapigenerator.exception;

public class ExceptionUserExists extends Exception {

    public ExceptionUserExists() {
        super("A user with that name already exists in the same company.");
    }

    public ExceptionUserExists(String message) {
        super(message);
    }

    public ExceptionUserExists(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionUserExists(Throwable cause) {
        super(cause);
    }

}
