package io.github.epi155.esql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ESqlDeleteBatch3<U, V, W> extends BatchAction {
    protected ESqlDeleteBatch3(PreparedStatement ps, int batchSize) {
        super(ps, batchSize);
    }

    /**
     * Append data for batch delete
     *
     * @param u first parameter for select data to be deleted
     * @param v second parameter for select data to be deleted
     * @param w third parameter for select data to be deleted
     * @throws SQLException SQL Error
     */
    public  abstract void lazyDelete(U u, V v, W w) throws SQLException;
}
