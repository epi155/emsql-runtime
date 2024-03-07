package io.github.epi155.emsql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SqlDeleteBatch4<U,V,W,X> extends BatchAction {
    protected SqlDeleteBatch4(String query, PreparedStatement ps, int batchSize) {
        super(query, ps, batchSize);
    }

    /**
     * Append data for batch delete
     *
     * @param u first parameter for select data to be deleted
     * @param v second parameter for select data to be deleted
     * @param w third parameter for select data to be deleted
     * @param x fourth parameter for select data to be called
     * @throws SQLException SQL Error
     */
    public  abstract void lazyDelete(U u, V v, W w, X x) throws SQLException;
}
