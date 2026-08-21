package io.github.epi155.emsql.fpe;

public final class PanFpe {

    private static final int MIN_PAN_LENGTH = 13;
    private static final int MAX_PAN_LENGTH = 19;

    private final byte[] key;
    private final byte[] tweak;

    public PanFpe(byte[] key, byte[] tweak) {
        this.key = FpeParams.key(key);
        this.tweak = FpeParams.tweak(tweak);
    }

    public String encrypt(String pan) {
        return crypt(validate(pan), false);
    }

    public String decrypt(String pan) {
        return crypt(validate(pan), true);
    }

    private String crypt(int[] digits, boolean decrypt) {
        int[] out = decrypt ? Ff1Core.decrypt(digits, 10, key, tweak) : Ff1Core.encrypt(digits, 10, key, tweak);
        StringBuilder sb = new StringBuilder(out.length);
        for (int d : out) {
            sb.append((char) ('0' + d));
        }
        return sb.toString();
    }

    private static int[] validate(String pan) {
        if (pan == null) {
            throw new IllegalArgumentException("pan must not be null");
        }
        int n = pan.length();
        if (n < MIN_PAN_LENGTH || n > MAX_PAN_LENGTH) {
            throw new IllegalArgumentException("pan must be between " + MIN_PAN_LENGTH + " and " + MAX_PAN_LENGTH + " digits");
        }
        int[] digits = new int[n];
        for (int i = 0; i < n; i++) {
            char c = pan.charAt(i);
            if (c < '0' || c > '9') {
                throw new IllegalArgumentException("pan must contain only digits");
            }
            digits[i] = c - '0';
        }
        return digits;
    }
}