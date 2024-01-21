package io.github.epi155.esql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ESqlInsertBatch<T> extends BatchAction {
    protected ESqlInsertBatch(PreparedStatement ps, int batchSize) {
        super(ps, batchSize);
    }

    public  abstract void lazyInsert(T t) throws SQLException;
}
