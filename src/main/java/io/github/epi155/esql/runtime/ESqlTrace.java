package io.github.epi155.esql.runtime;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.function.Supplier;

@Slf4j
public class ESqlTrace {
    public static void showQuery(String query, Supplier<Object[]> listSupplier) {
        log.debug("Query: {}", query);
        if (log.isTraceEnabled()) {
            Object[] parms = listSupplier.get();
            int k= 0;
            for(val parm: parms) {
                log.trace("i[{}] = {}", ++k, parm);
            }
        }
    }
}
