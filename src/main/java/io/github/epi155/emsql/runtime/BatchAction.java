package io.github.epi155.emsql.runtime;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
abstract class  BatchAction implements AutoCloseable {
    protected final PreparedStatement ps;
    private final int batchSize;
    private final String query;
    @Setter
    private EConsumer<int[]> trigger;
    private int pending = 0;
    private final Lock lock = new ReentrantLock();

    protected BatchAction(String query, PreparedStatement ps, int batchSize) {
        this.query = query;
        this.ps = ps;
        this.batchSize = batchSize;
    }

    protected void addBatch() throws SQLException {
        lock.lock();
        try {
            ps.addBatch();
            pending++;
        } finally {
            lock.unlock();
        }
        log.debug("Queued {}/{}", pending, batchSize);
        if (pending >= batchSize)
            flush();
    }

    public void flush() throws SQLException {
        if (pending > 0) {
            log.debug("Executing {}x Query Batch {} ...", pending, query);
            int[] n;
            lock.lock();    // stop queuing
            try {
                n = ps.executeBatch();
                pending = 0;
            } finally {
                lock.unlock();
            }
            log.debug("Executed batch {}.", n.length);
            if (trigger != null) {
                trigger.accept(n);
            }
        }
    }

    public void close() throws SQLException {
        flush();
        ps.close();
    }

}
