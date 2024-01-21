package io.github.epi155.esql.runtime;

import java.sql.SQLException;

public interface ESqlCursor<T> extends AutoCloseable {
    boolean hasNext() throws SQLException;
    T next() throws SQLException;
    @Override
    void close() throws SQLException;
}
