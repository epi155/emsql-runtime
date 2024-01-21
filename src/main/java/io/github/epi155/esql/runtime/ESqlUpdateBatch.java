package io.github.epi155.esql.runtime;

import java.sql.SQLException;

public interface ESqlUpdateBatch<T> extends AutoCloseable {
    void lazyUpdate(T t) throws SQLException;
    void flush() throws SQLException;
    @Override
    void close() throws SQLException;
}
