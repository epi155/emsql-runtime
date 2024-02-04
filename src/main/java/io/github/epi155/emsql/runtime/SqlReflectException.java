package io.github.epi155.emsql.runtime;

public class SqlReflectException extends RuntimeException {
    SqlReflectException(String message) {
        super(message);
    }
    SqlReflectException(String message, Throwable cause) {
        super(message, cause);
    }
}
