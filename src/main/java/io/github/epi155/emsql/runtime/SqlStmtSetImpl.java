package io.github.epi155.emsql.runtime;

import java.sql.SQLException;
import java.sql.Statement;

public class SqlStmtSetImpl implements SqlStatementSetter {
    private final Statement statement;

    public SqlStmtSetImpl(Statement statement) {
        this.statement = statement;
    }
    @Override
    public void setFetchSize(int rows) throws SQLException {
        statement.setFetchSize(rows);
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        statement.setMaxFieldSize(max);
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        statement.setMaxRows(max);
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        statement.setQueryTimeout(seconds);
    }
}
