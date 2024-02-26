package io.github.epi155.emsql.runtime;

import java.util.NoSuchElementException;

public class EOptional<T> {
    private final T value;

    private EOptional(T value) {
        this.value = value;
    }

    public static <S> EOptional<S> of(S value) {
        if (value==null)
            throw new NullPointerException("Argument is null");
        return new EOptional<S>(value);
    }
    public static <S> EOptional<S> empty() {
        return new EOptional<S>(null);
    }
    public static <S> EOptional<S> ofNullable(S value) {
        return new EOptional<S>(value);
    }
    public boolean isPresent() {
        return value!=null;
    }
    public T get() {
        if (value == null)
            throw new NoSuchElementException();
        return value;
    }
    public T orElse(T other) {
        if (value == null) {
            if (other == null)
                throw new NullPointerException("Argument is null");
            return other;
        }
        return value;
    }
}
