package io.github.epi155.emsql.runtime;

import java.sql.CallableStatement;
import java.sql.SQLException;

public abstract class SqlCallBatch<I> extends BatchAction {
    protected SqlCallBatch(String query, CallableStatement cs, int batchSize) {
        super(query, cs, batchSize);
    }

    /**
     * Append data for batch call
     * @param i key (or wrapper class) for select data to be called
     * @throws SQLException SQL Error
     */
    public  abstract void lazyCall(I i) throws SQLException;
}
