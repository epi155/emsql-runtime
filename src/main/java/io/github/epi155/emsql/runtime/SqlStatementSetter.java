package io.github.epi155.emsql.runtime;

import java.sql.SQLException;

public interface SqlStatementSetter {
    void 	setFetchSize(int rows) throws SQLException;
    void 	setMaxFieldSize(int max) throws SQLException;
    void 	setMaxRows(int max) throws SQLException;
    void 	setQueryTimeout(int seconds) throws SQLException;
}
