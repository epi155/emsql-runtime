package io.github.epi155.emsql.runtime;

import java.sql.CallableStatement;
import java.sql.SQLException;

public abstract class SqlInlineBatch1<I> extends BatchAction {
    protected SqlInlineBatch1(String query, CallableStatement cs, int batchSize) {
        super(query, cs, batchSize);
    }

    /**
     * Append data for batch inline call
     * @param i key (or wrapper class) for select data to be called
     * @throws SQLException SQL Error
     */
    public  abstract void lazyInline(I i) throws SQLException;
}
