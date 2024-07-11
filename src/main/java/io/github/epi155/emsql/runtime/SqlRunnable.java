package io.github.epi155.emsql.runtime;

import java.sql.SQLException;

public interface SqlRunnable {
    void run() throws SQLException;
}
