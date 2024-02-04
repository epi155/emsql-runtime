package io.github.epi155.emsql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SqlInsertBatch2<U,V> extends BatchAction {
    protected SqlInsertBatch2(PreparedStatement ps, int batchSize) {
        super(ps, batchSize);
    }

    /**
     * Append data for batch insert
     *
     * @param u first column value
     * @param v second column value
     * @throws SQLException SQL Error
     */
    public  abstract void lazyInsert(U u, V v) throws SQLException;
}
