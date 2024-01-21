package io.github.epi155.esql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ESqlDeleteBatch <T> extends BatchAction {
    protected ESqlDeleteBatch(PreparedStatement ps, int batchSize) {
        super(ps, batchSize);
    }

    public  abstract void lazyDelete(T t) throws SQLException;
}
