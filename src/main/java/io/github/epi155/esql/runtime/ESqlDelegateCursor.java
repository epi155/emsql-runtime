package io.github.epi155.esql.runtime;

import java.sql.SQLException;

public interface ESqlDelegateCursor extends AutoCloseable {
    boolean hasNext() throws SQLException;
    void next() throws SQLException;
    @Override
    void close() throws SQLException;
}
