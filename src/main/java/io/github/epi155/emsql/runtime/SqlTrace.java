package io.github.epi155.emsql.runtime;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

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
    public static void showResult(SqlArg ...args) {
        if (log.isTraceEnabled()) {
            int k= 0;
            for(val arg: args) {
                log.trace("o{}) {}: {} = {}", ++k, arg.name, arg.type, arg.value);
            }
        }
    }
}
