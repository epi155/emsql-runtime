package io.github.epi155.esql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ESqlInsertBatch<I> extends BatchAction {
    protected ESqlInsertBatch(PreparedStatement ps, int batchSize) {
        super(ps, batchSize);
    }

    /**
     * Append data for batch insert
     *
     * @param i data to be inserted
     * @throws SQLException SQL Error
     */
    public  abstract void lazyInsert(I i) throws SQLException;
}
