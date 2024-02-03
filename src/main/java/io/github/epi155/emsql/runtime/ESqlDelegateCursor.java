package io.github.epi155.emsql.runtime;

import java.sql.SQLException;

public interface ESqlDelegateCursor extends AutoCloseable {
    boolean hasNext() throws SQLException;
    void fetchNext() throws SQLException;
    @Override
    void close() throws SQLException;
}
