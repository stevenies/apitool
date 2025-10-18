package com.smn.restapigenerator.exception;

public class ExceptionUserDoesntExist extends Exception {

    public ExceptionUserDoesntExist() {
        super("A user with that name already exists in the same company.");
    }

    public ExceptionUserDoesntExist(String message) {
        super(message);
    }

    public ExceptionUserDoesntExist(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionUserDoesntExist(Throwable cause) {
        super(cause);
    }

}
