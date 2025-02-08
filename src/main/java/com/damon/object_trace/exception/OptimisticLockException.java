package com.damon.object_trace.exception;

public class OptimisticLockException extends ObjectTraceException {
    public OptimisticLockException(String message) {
        super(message);
    }

    public OptimisticLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptimisticLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public OptimisticLockException() {
    }

    public OptimisticLockException(Throwable cause) {
        super(cause);
    }
}
