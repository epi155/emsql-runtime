package io.github.epi155.esql.runtime;

import java.sql.SQLException;

public interface ESqlDeleteBatch <T> extends AutoCloseable {
    void lazyDelete(T t) throws SQLException;
    void flush() throws SQLException;
    @Override
    void close() throws SQLException;
}
