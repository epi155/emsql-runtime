package io.github.epi155.esql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ESqlUpdateBatch<I> extends BatchAction {
    protected ESqlUpdateBatch(PreparedStatement ps, int batchSize) {
        super(ps, batchSize);
    }

    /**
     * Append data for batch update
     * @param i key (or wrapper class) for select data to be updated
     * @throws SQLException SQL Error
     */
    public  abstract void lazyUpdate(I i) throws SQLException;
}
