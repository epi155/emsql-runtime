package io.github.epi155.emsql.runtime;

import java.sql.CallableStatement;
import java.sql.SQLException;

public abstract class SqlInlineBatch3<U,V,W> extends BatchAction {
    protected SqlInlineBatch3(String query, CallableStatement cs, int batchSize) {
        super(query, cs, batchSize);
    }

    /**
     * Append data for batch inline call
     *
     * @param u first parameter for select data to be called
     * @param v second parameter for select data to be called
     * @param w third parameter for select data to be called
     * @throws SQLException SQL Error
     */
    public  abstract void lazyInline(U u, V v, W w) throws SQLException;
}
