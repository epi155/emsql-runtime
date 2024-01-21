package io.github.epi155.esql.runtime;

import java.sql.SQLException;

public interface ESqlInsertBatch<T> extends AutoCloseable {
    void lazyInsert(T t) throws SQLException;
    void flush() throws SQLException;
    @Override
    void close() throws SQLException;
}
