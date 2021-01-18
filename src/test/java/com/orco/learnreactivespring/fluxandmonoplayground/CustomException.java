package com.orco.learnreactivespring.fluxandmonoplayground;

public class CustomException extends Throwable {

    private String message;

    public CustomException(Throwable cause) {
        this.message = cause.getMessage();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
