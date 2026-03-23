package io.github.epi155.emsql.runtime;

import java.sql.SQLWarning;

import static io.github.epi155.emsql.runtime.SqlCode.P100;

public class SqlNoResultException extends SQLWarning {
    public SqlNoResultException() {
        super(P100.reason, P100.state, P100.code);
    }
}
