package io.github.epi155.esql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ESqlUpdateBatch<T> extends BatchAction {
    protected ESqlUpdateBatch(PreparedStatement ps, int batchSize) {
        super(ps, batchSize);
    }

    public  abstract void lazyUpdate(T t) throws SQLException;
}
