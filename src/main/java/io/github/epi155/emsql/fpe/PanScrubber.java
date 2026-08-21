package io.github.epi155.emsql.fpe;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class PanScrubber {

    static final String LUHN_PROPERTY = "emsql.luhn";
    static final String ARGS_PROPERTY = "emsql.trace";

    private static final Pattern PAN_PATTERN = Pattern.compile("(?<!\\d)\\d{13,19}(?!\\d)");

    private final boolean luhnCheck;
    private final boolean argsMask;

    PanScrubber(boolean luhnCheck, boolean argsMask) {
        this.luhnCheck = luhnCheck;
        this.argsMask = argsMask;
    }

    final String scrub(String text) {
        if (text == null) {
            return null;
        }
        Matcher m = PAN_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer(text.length());
        while (m.find()) {
            String candidate = m.group();
            if (!luhnCheck || luhn(candidate)) {
                // java.lang.NoSuchMethodError: java/util/regex/Matcher.appendReplacement(Ljava/lang/StringBuilder;Ljava/lang/String;)
                m.appendReplacement(sb, Matcher.quoteReplacement(transform(candidate)));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    abstract String transform(String pan);

    static boolean luhnCheck(Properties props) {
        String value = props.getProperty(LUHN_PROPERTY);
        if (value == null || value.isEmpty()) {
            return true;
        }
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("property '" + LUHN_PROPERTY + "' must be 'true' or 'false', got: " + value);
    }

    static boolean argsMask(Properties props) {
        String value = props.getProperty(ARGS_PROPERTY);
        if (value == null || value.isEmpty()) {
            return true;
        }
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("property '" + ARGS_PROPERTY + "' must be 'true' or 'false', got: " + value);
    }

    public static boolean luhn(String digits) {
        if (digits == null || digits.isEmpty()) {
            return false;
        }
        int sum = 0;
        boolean doubleDigit = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (doubleDigit) {
                d *= 2;
                if (d > 9) {
                    d -= 9;
                }
            }
            sum += d;
            doubleDigit = !doubleDigit;
        }
        return sum % 10 == 0;
    }

    abstract String decrypt(String pan);
    String encrypt(String pan) {
        return transform(pan);
    }

    Object scrubArg(Object o) {
        if (o instanceof String && argsMask) {
            return scrub((String) o);
        }
        if (o instanceof Long && argsMask) {
            return scrub(o.toString());
        }
        return o;
    }
}
