package com.damon.aggregate.persistence.exception;

public class AggregatePersistenceException extends RuntimeException {
    public AggregatePersistenceException(String message) {
        super(message);
    }

    public AggregatePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AggregatePersistenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AggregatePersistenceException() {
    }

    public AggregatePersistenceException(Throwable cause) {
        super(cause);
    }
}
