package com.simple.api.error;

public class RemoteServiceException extends RuntimeException {
    private final int errorCode;

    public RemoteServiceException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}