package io.github.epi155.emsql.fpe;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
final class PciConfig {

    private static final String RESOURCE = "emsql-runtime.properties";
    private static final String MODE_PROPERTY = "emsql.mode";
    private static final String MODE_ENCRYPT = "encrypt";
    private static final String MODE_MASK = "mask";
    private static final String MODE_NONE = "none";

    private PciConfig() {
    }

    static PanScrubber get() {
        return Holder.SCRUBBER;
    }

    static void reload(String resource) {
        Holder.SCRUBBER = load(resource);
    }

    private static final class Holder {
        private static volatile PanScrubber SCRUBBER = load(RESOURCE);
    }

    static PanScrubber load(String resource) {
        InputStream is = PciConfig.class.getClassLoader().getResourceAsStream(resource);
        if (is == null) {
            log.warn("PAN sanitization disabled: configuration resource '{}' not found on classpath; output = input", resource);
            return null;
        }
        try {
            Properties props = new Properties();
            try {
                props.load(is);
            } catch (IOException e) {
                throw new FpeException("Cannot load configuration resource: " + resource, e);
            }
            String mode = props.getProperty(MODE_PROPERTY);
            if (mode == null || mode.isEmpty()) {
                log.warn("PAN sanitization disabled: property '{}' missing in resource '{}'; output = input", MODE_PROPERTY, resource);
                return null;
            }
            if (MODE_NONE.equals(mode)) {
                return null;
            }
            if (MODE_MASK.equals(mode)) {
                return new PanMasker(PanScrubber.luhnCheck(props), PanScrubber.argsMask(props));
            }
            if (MODE_ENCRYPT.equals(mode)) {
                return new PanSanitizer(props);
            }
            log.warn("PAN sanitization disabled: unknown {} '{}' in resource '{}'; output = input", MODE_PROPERTY, mode, resource);
            return null;
        } finally {
            close(is);
        }
    }

    private static void close(InputStream is) {
        try {
            is.close();
        } catch (IOException ignored) {
        }
    }
}
