package io.github.epi155.emsql.runtime;

import java.util.Set;

class FlushContext {
    private FlushContext() {}
    static final ThreadLocal<Set<String>> context = new ThreadLocal<>();
}
