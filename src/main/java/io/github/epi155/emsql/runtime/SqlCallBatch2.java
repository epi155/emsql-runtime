package io.github.epi155.emsql.runtime;

import java.sql.CallableStatement;
import java.sql.SQLException;

public abstract class SqlCallBatch2<U,V> extends BatchAction {
    protected SqlCallBatch2(String query, CallableStatement cs, int batchSize) {
        super(query, cs, batchSize);
    }

    /**
     * Append data for batch call
     *
     * @param u first parameter for select data to be called
     * @param v second parameter for select data to be called
     * @throws SQLException SQL Error
     */
    public  abstract void lazyCall(U u, V v) throws SQLException;
}
