package io.github.epi155.esql.runtime;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class ESqlTrace {
    public static void showQuery(String query, ESupplier<Object[]> listSupplier) {
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
