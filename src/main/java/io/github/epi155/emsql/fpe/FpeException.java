package io.github.epi155.emsql.fpe;

public class FpeException extends RuntimeException {

    public FpeException(String message) {
        super(message);
    }

    public FpeException(String message, Throwable cause) {
        super(message, cause);
    }
}
