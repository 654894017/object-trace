package com.damon.aggregate.persistence.exception;

public class OptimisticLockException extends RuntimeException {
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
