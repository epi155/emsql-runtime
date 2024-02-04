package io.github.epi155.emsql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SqlDeleteBatch2<U, V> extends BatchAction {
    protected SqlDeleteBatch2(PreparedStatement ps, int batchSize) {
        super(ps, batchSize);
    }

    /**
     * Append data for batch delete
     *
     * @param u first parameter for select data to be deleted
     * @param v second parameter for select data to be deleted
     * @throws SQLException SQL Error
     */
    public  abstract void lazyDelete(U u, V v) throws SQLException;
}
