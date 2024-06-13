package io.github.epi155.emsql.runtime;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
abstract class  BatchAction implements AutoCloseable {
    protected final PreparedStatement ps;
    private final int batchSize;
    private final String query;
    @Setter
    private EConsumer<int[]> trigger;
    @Setter(AccessLevel.PROTECTED)
    private Runnable afterFlush;
    private int pending = 0;

    protected BatchAction(String query, PreparedStatement ps, int batchSize) {
        this.query = query;
        this.ps = ps;
        this.batchSize = batchSize;
    }

    protected void addBatch() throws SQLException {
        ps.addBatch();
        pending++;
        log.debug("Queued {}/{}", pending, batchSize);
        if (pending >= batchSize)
            flush();
    }

    public void flush() throws SQLException {
        if (pending > 0) {
            if (log.isDebugEnabled()) {
                log.debug("Executing {}x Query Batch {} ...", pending, query);
            } else {
                log.info("Executing {}x Query Batch {} ...", pending, this.getClass().getSimpleName());
            }
            int[] n;
            try {
                n = ps.executeBatch();
                pending = 0;
            } catch (BatchUpdateException e) {
                int[] updateCount = e.getUpdateCounts();
                for(int k=0; k<updateCount.length; k++) {
                    int code = updateCount[k];
                    if (code == Statement.EXECUTE_FAILED) {
                        log.error("Error on request {}/{}, State: {}, Mesg: {}", k+1, pending, e.getSQLState(), e.getMessage());
                    }
                }
                throw e;
            }
            log.debug("Executed batch {}.", n.length);
            if (trigger != null) {
                trigger.accept(n);
            }
            if (afterFlush != null) {
                afterFlush.run();
            }
        }
    }

    public void close() throws SQLException {
        flush();
        ps.close();
    }

}
