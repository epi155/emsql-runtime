package io.github.epi155.esql.runtime;

import java.util.NoSuchElementException;

public class EOptional<T> {
    private final T value;

    private EOptional(T value) {
        this.value = value;
    }

    public static <S> EOptional<S> of(S value) {
        return new EOptional<S>(value);
    }
    public static <S> EOptional<S> empty() {
        return new EOptional<S>(null);
    }
    public boolean isPresent() {
        return value!=null;
    }
    public T get() {
        if (value == null)
            throw new NoSuchElementException();
        return value;
    }
}
