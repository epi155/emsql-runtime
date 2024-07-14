package io.github.epi155.emsql.runtime;

import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.sql.Statement.SUCCESS_NO_INFO;

@Slf4j
abstract class  BatchAction implements AutoCloseable {
    protected final PreparedStatement ps;
    private final int batchSize;
    private final String query;
    @Setter
    private EConsumer<int[]> trigger;
    private SqlRunnable[] beforeFlush = {};
    private SqlRunnable[] afterFlush = {};
    private int pending = 0;

    protected BatchAction(String query, PreparedStatement ps, int batchSize) {
        this.query = query;
        this.ps = ps;
        this.batchSize = batchSize;
    }
    public void beforeFlush(@NonNull SqlRunnable ... actions) {
        beforeFlush = actions;
    }
    public void afterFlush(@NonNull SqlRunnable ... actions) {
        afterFlush = actions;
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
            String className = this.getClass().getName();
            Set<String> set = FlushContext.context.get();
            try {
                if (set == null) {
                    set = new HashSet<>(Collections.singleton(className)); // mutable set
                    FlushContext.context.set(set);
                    doFlush();
                } else {
                    if (set.contains(className)) {
                        log.warn("Circular flush for {}, break", className);
                    } else {
                        set.add(className);
                        doFlush();
                    }
                }
            } finally {
                if (set != null) {
                    set.remove(className);
                    if (set.isEmpty()) {
                        FlushContext.context.remove();
                    }
                }
            }
        }
    }

    private void doFlush() throws SQLException {
        for (val action : beforeFlush)
            action.run();
        if (log.isDebugEnabled()) {
            log.debug("Executing {}x Query Batch {} ...", pending, query);
        } else {
            log.info("Executing {}x Query Batch {} ...", pending, this.getClass().getSimpleName());
        }
        try {
            int[] n = ps.executeBatch();
            pending = 0;
            log.debug("Executed batch {}.", n.length);
            if (trigger != null) {
                trigger.accept(n);
            }
            for (val action : afterFlush)
                action.run();
        } catch (BatchUpdateException e) {
            notifyError(e);
            throw e;
        }
    }

    public static int updateCount(int[] numUpdates) {
        int sum = 0;
        int threshold = 5;
        for (int i=0; i < numUpdates.length; i++) {
            if (numUpdates[i] == SUCCESS_NO_INFO) {
                log.warn("Execution {}: unknown number of rows updated", i);
                if (--threshold < 0)
                    return SUCCESS_NO_INFO; /* -2 */
            } else {
                log.debug("Execution {} successful: {} rows updated", i, numUpdates[i]);
                sum += numUpdates[i];
            }
        }
        return sum;
    }

    private void notifyError(BatchUpdateException e) {
        int[] updateCount = e.getUpdateCounts();
        if (updateCount.length == 0) {
            log.error("Error on request (not available)/{}, State: {}, Mesg: {}", pending, e.getSQLState(), e.getMessage());
        } else {
            for(int k=0; k<updateCount.length; k++) {
                int code = updateCount[k];
                if (code == Statement.EXECUTE_FAILED) {
                    log.error("Error on request {}/{}, State: {}, Mesg: {}", k+1, pending, e.getSQLState(), e.getMessage());
                }
            }
        }
    }

    public void close() throws SQLException {
        flush();
        ps.close();
    }

}
