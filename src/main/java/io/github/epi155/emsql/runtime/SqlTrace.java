package io.github.epi155.emsql.runtime;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

@Slf4j
public class SqlTrace {
    public static void showQuery(String query, ESupplier<SqlArg[]> listArgs) {
        log.debug("Query: {}", query);
        if (log.isTraceEnabled()) {
            SqlArg[] args = listArgs.get();
            int k= 0;
            for(val arg: args) {
                log.trace("i{}) {}: {} = {}", ++k, arg.name, arg.type, arg.value);
            }
        }
    }

    public static void showCause(SQLException e, String query, ESupplier<SqlArg[]> listArgs) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.printf("SqlError> SqlCode: %+d, SqlState: %s%n", e.getErrorCode(), e.getSQLState());
        pw.printf("Query: %s%n", query);
        SqlArg[] args = listArgs.get();
        int k= 0;
        for(val arg: args) {
            pw.printf("%d) %s: %s = %s%n", ++k, arg.name, arg.type, arg.value);
        }
        String cause = sw.toString();
        log.error(cause, e);
        e.setNextException(new SQLException(cause));
    }

    public static void showResult(SqlArg ...args) {
        if (log.isTraceEnabled()) {
            int k= 0;
            for(val arg: args) {
                log.trace("o{}) {}: {} = {}", ++k, arg.name, arg.type, arg.value);
            }
        }
    }
}
