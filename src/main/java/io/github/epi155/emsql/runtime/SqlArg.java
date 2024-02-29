package io.github.epi155.emsql.runtime;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SqlArg {
    public final String name;
    public final String type;
    public final Object value;
}
