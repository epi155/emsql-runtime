package io.github.epi155.emsql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SqlInsertBatch4<U,V,W,X> extends BatchAction {
    protected SqlInsertBatch4(String query, PreparedStatement ps, int batchSize) {
        super(query, ps, batchSize);
    }

    /**
     * Append data for batch insert
     *
     * @param u first column value
     * @param v second column value
     * @param w third column value
     * @param x fourth column value
     * @throws SQLException SQL Error
     */
    public  abstract void lazyInsert(U u, V v, W w, X x) throws SQLException;
}
