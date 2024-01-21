package io.github.epi155.esql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ESqlDeleteBatch3<U, V, W> extends BatchAction {
    protected ESqlDeleteBatch3(PreparedStatement ps, int batchSize) {
        super(ps, batchSize);
    }

    public  abstract void lazyDelete(U u, V v, W w) throws SQLException;
}
