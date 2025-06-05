package com.smn.apitool.dto;

public class HelloDTO {

    private String message;

    @SuppressWarnings("unused")
    public HelloDTO() {
    }

    public HelloDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
