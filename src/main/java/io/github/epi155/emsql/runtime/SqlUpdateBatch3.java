package io.github.epi155.emsql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SqlUpdateBatch3<U,V,W> extends BatchAction {
    protected SqlUpdateBatch3(String query, PreparedStatement ps, int batchSize) {
        super(query, ps, batchSize);
    }

    /**
     * Append data for batch update
     *
     * @param u first parameter for select data to be updated
     * @param v second parameter for select data to be updated
     * @param w third parameter for select data to be updated
     * @throws SQLException SQL Error
     */
    public  abstract void lazyUpdate(U u, V v, W w) throws SQLException;
}
