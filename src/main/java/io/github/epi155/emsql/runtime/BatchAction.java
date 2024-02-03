package io.github.epi155.emsql.runtime;

import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

abstract class  BatchAction implements AutoCloseable {
    protected final PreparedStatement ps;
    private final int batchSize;
    @Setter
    private EConsumer<int[]> trigger;
    private int pending = 0;

    protected BatchAction(PreparedStatement ps, int batchSize) {
        this.ps = ps;
        this.batchSize = batchSize;
    }

    protected void addBatch() throws SQLException {
        ps.addBatch();
        if (++pending > batchSize)
            flush();

    }

    public void flush() throws SQLException {
        if (pending > 0) {
            int[] n = ps.executeBatch();
            if (trigger != null) {
                trigger.accept(n);
            }
        }
        pending = 0;
    }

    public void close() throws SQLException {
        flush();
        ps.close();
    }

}
