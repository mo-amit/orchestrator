package com.ingestyon.orchestrator.processor;


public abstract class BaseProcessor implements Runnable {
    public static final String SUCCESS_PROCESSING_STATUS = "SUCCESS";
    public static final String COMPLETED_WITH_ERROR = "COMPLETED_WITH_ERROR";
    public static final String FAILURE_PROCESSING_STATUS = "FAILED";
    public static final String DUPLICATE = "DUPLICATE";
    public static final String IN_PROGRESS_PROCESSING_STATUS = "PROCESSING";
}
