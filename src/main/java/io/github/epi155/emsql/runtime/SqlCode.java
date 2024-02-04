package io.github.epi155.emsql.runtime;

import java.sql.SQLException;

public enum SqlCode {
    N811("More than one row", "21000", -811),
    P100("No data was found", "02000", +100),
    ;

    private final SQLException e;

    SqlCode(String message, String state, int code) {
        this.e = new SQLException(message, state, code);
    }

    public SQLException getInstance() {
        return e;
    }
}
