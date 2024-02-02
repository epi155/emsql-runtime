package io.github.epi155.esql.runtime;

public interface EConsumer<O> {
    void accept(O o);
}
