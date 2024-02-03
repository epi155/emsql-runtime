package io.github.epi155.emsql.runtime;

public interface EConsumer<O> {
    void accept(O o);
}
