package io.github.epi155.emsql.runtime;

import java.sql.SQLException;

enum SqlCode {
    N811("More than one row", "21000", -811),
    P100("No data was found", "02000", +100),
    ;

    public final int code;
    public final String state;
    public final String reason;

    SqlCode(String reason, String state, int code) {
        this.code = code;
        this.state = state;
        this.reason = reason;
    }
}
