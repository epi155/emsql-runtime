package io.github.epi155.emsql.fpe;

import java.util.Properties;

public final class PanSanitizer extends PanScrubber {

    private static final String KEY_PROPERTY = "emsql.fpe.key";
    private static final String TWEAK_PROPERTY = "emsql.fpe.tweak";

    private final PanFpe fpe;

    PanSanitizer(Properties props) {
        this(Config.from(props), PanScrubber.luhnCheck(props), PanScrubber.argsMask(props));
    }

    PanSanitizer(byte[] key, byte[] tweak, boolean luhnCheck, boolean argsMask) {
        super(luhnCheck, argsMask);
        this.fpe = new PanFpe(key, tweak);
    }

    private PanSanitizer(Config config, boolean luhnCheck, boolean argsMask) {
        this(config.key, config.tweak, luhnCheck, argsMask);
    }

    @Override
    String transform(String pan) {
        return fpe.encrypt(pan);
    }

    @Override
    String decrypt(String pan) {
        return fpe.decrypt(pan);
    }

    private static byte[] hex(String s) {
        if (s.length() % 2 != 0) {
            throw new IllegalArgumentException("hex value must have even length: " + s);
        }
        byte[] out = new byte[s.length() / 2];
        for (int i = 0; i < out.length; i++) {
            int hi = hexDigit(s.charAt(i * 2));
            int lo = hexDigit(s.charAt(i * 2 + 1));
            if (hi < 0 || lo < 0) {
                throw new IllegalArgumentException("invalid hex value: " + s);
            }
            out[i] = (byte) ((hi << 4) | lo);
        }
        return out;
    }

    private static int hexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        return -1;
    }

    private static final class Config {

        private final byte[] key;
        private final byte[] tweak;

        private Config(byte[] key, byte[] tweak) {
            this.key = key;
            this.tweak = tweak;
        }

        private static Config from(Properties props) {
            String key = props.getProperty(KEY_PROPERTY);
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("missing property '" + KEY_PROPERTY + "' in configuration");
            }
            String tweak = props.getProperty(TWEAK_PROPERTY);
            return new Config(hex(key), tweak == null ? new byte[0] : hex(tweak));
        }
    }
}
