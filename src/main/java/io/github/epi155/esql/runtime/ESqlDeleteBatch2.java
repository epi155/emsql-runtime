package io.github.epi155.esql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ESqlDeleteBatch2<U, V> extends BatchAction {
    protected ESqlDeleteBatch2(PreparedStatement ps, int batchSize) {
        super(ps, batchSize);
    }

    public  abstract void lazyDelete(U u, V v) throws SQLException;
}
