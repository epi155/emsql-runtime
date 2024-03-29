package io.github.epi155.emsql.runtime;

import java.sql.SQLException;

public class EmSqlException extends RuntimeException {
    public EmSqlException(SQLException e) {
        super(e);
    }
}
