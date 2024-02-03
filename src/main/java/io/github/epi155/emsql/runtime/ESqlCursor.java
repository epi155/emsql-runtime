package io.github.epi155.emsql.runtime;

import java.sql.SQLException;

public interface ESqlCursor<T> extends AutoCloseable {
    boolean hasNext() throws SQLException;
    T fetchNext() throws SQLException;
    @Override
    void close() throws SQLException;
}
