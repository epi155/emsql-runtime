package io.github.epi155.esql.runtime;

import java.sql.SQLException;

public interface ESqlInsertBatch2<U,V> extends AutoCloseable {
    void lazyInsert(U u, V v) throws SQLException;
    void flush() throws SQLException;
    @Override
    void close() throws SQLException;
}
