package io.github.epi155.esql.test;

import io.github.epi155.emsql.runtime.ESQL;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class TestReflect {

    @Data
    public static class LoBool {
        private boolean one;
        private Boolean two;
    }
    @Data
    public static class HiBool {
        private boolean one;
        private Boolean two;
        private LoBool next;
    }

    @Test
    void testBoolean1() {
        HiBool hi = new HiBool();
        ESQL.set(hi, "one", true);
        boolean v = ESQL.get(hi, "one", boolean.class);
        log.info("get result: {}", v);
        Assertions.assertTrue(v);
    }
    @Test
    void testBoolean2() {
        HiBool hi = new HiBool();
        ESQL.set(hi, "two", null);
        Boolean v = ESQL.get(hi, "two", Boolean.class);
        log.info("get result: {}", v);
        Assertions.assertNull(v);
    }

    @Test
    void testBoolean11() {
        HiBool hi = new HiBool();
        ESQL.set(hi, "next.one", true);
        boolean v = ESQL.get(hi, "next.one", boolean.class);
        log.info("get result: {}", v);
        Assertions.assertTrue(v);
    }
    @Test
    void testBoolean12() {
        HiBool hi = new HiBool();
        ESQL.set(hi, "next.two", null);
        Boolean v = ESQL.get(hi, "next.two", Boolean.class);
        log.info("get result: {}", v);
        Assertions.assertNull(v);
    }
}
