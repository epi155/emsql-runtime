package io.github.epi155.emsql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ESqlDeleteBatch <I> extends BatchAction {
    protected ESqlDeleteBatch(PreparedStatement ps, int batchSize) {
        super(ps, batchSize);
    }

    /**
     * Append data for batch delete
     *
     * @param i parameter (or wrapper class) for select data to be deleted
     * @throws SQLException SQL Error
     */
    public  abstract void lazyDelete(I i) throws SQLException;
}
