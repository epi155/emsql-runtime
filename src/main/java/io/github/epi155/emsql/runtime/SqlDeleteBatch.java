package io.github.epi155.emsql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SqlDeleteBatch<I> extends BatchAction {
    protected SqlDeleteBatch(String query, PreparedStatement ps, int batchSize) {
        super(query, ps, batchSize);
    }

    /**
     * Append data for batch delete
     *
     * @param i parameter (or wrapper class) for select data to be deleted
     * @throws SQLException SQL Error
     */
    public  abstract void lazyDelete(I i) throws SQLException;
}
