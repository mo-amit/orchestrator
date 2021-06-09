package com.ingestyon.orchestrator.exception;

public  class BaseException extends RuntimeException {
    private final String internalErrorCode ;

    public BaseException (String internalErrorCode, String message) {
        super(message);
        this.internalErrorCode = internalErrorCode;
    }
    public BaseException ( String message) {
        super(message);
        internalErrorCode = null;
    }


}
