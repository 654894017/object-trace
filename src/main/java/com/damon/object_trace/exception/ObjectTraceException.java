package com.damon.object_trace.exception;

public class ObjectTraceException extends RuntimeException {
    public ObjectTraceException(String message) {
        super(message);
    }

    public ObjectTraceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectTraceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ObjectTraceException() {
    }

    public ObjectTraceException(Throwable cause) {
        super(cause);
    }
}
