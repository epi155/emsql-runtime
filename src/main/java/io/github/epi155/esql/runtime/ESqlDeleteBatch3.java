package io.github.epi155.esql.runtime;

import java.sql.SQLException;

public interface ESqlDeleteBatch3<U, V, W> extends AutoCloseable {
    void lazyDelete(U u, V v, W w) throws SQLException;
    void flush() throws SQLException;
    @Override
    void close() throws SQLException;
}
