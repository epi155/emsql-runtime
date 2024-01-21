package io.github.epi155.esql.runtime;

public class ESqlReflectException extends RuntimeException {
    ESqlReflectException(String message) {
        super(message);
    }
    ESqlReflectException(String message, Throwable cause) {
        super(message, cause);
    }
}
