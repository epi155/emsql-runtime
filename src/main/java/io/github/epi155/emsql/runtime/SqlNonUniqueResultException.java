package io.github.epi155.emsql.runtime;

import java.sql.SQLException;

import static io.github.epi155.emsql.runtime.SqlCode.N811;

public class SqlNonUniqueResultException extends SQLException {
    public SqlNonUniqueResultException() {
        super(N811.reason, N811.state, N811.code);
    }
}
