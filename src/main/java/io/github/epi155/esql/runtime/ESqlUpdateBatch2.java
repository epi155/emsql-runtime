package io.github.epi155.esql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ESqlUpdateBatch2<U,V> extends BatchAction {
    protected ESqlUpdateBatch2(PreparedStatement ps, int batchSize) {
        super(ps, batchSize);
    }

    /**
     * Append data for batch update
     *
     * @param u first parameter for select data to be updated
     * @param v second parameter for select data to be updated
     * @throws SQLException SQL Error
     */
    public  abstract void lazyUpdate(U u, V v) throws SQLException;
}
