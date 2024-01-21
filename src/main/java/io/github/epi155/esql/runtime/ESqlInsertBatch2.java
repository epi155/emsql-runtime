package io.github.epi155.esql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ESqlInsertBatch2<U,V> extends BatchAction {
    protected ESqlInsertBatch2(PreparedStatement ps, int batchSize) {
        super(ps, batchSize);
    }

    public  abstract void lazyInsert(U u, V v) throws SQLException;
}
