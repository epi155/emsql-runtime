package io.github.epi155.emsql.fpe;

import java.security.GeneralSecurityException;
import javax.crypto.Cipher;

final class FpeParams {

    static final int MAX_TWEAK_LENGTH = 8;

    private FpeParams() {
    }

    static byte[] key(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }
        if (key.length != 16 && key.length != 24 && key.length != 32) {
            throw new IllegalArgumentException("key must be 16, 24 or 32 bytes (AES-128/192/256)");
        }
        int maxBits;
        try {
            maxBits = Cipher.getMaxAllowedKeyLength("AES");
        } catch (GeneralSecurityException e) {
            throw new FpeException("Cannot query AES key length limits", e);
        }
        if (maxBits < key.length * 8) {
            throw new FpeException("AES-" + (key.length * 8) + " not available: JCE jurisdiction policy limits AES to " + maxBits + " bits");
        }
        return key.clone();
    }

    static byte[] tweak(byte[] tweak) {
        if (tweak == null) {
            throw new IllegalArgumentException("tweak must not be null");
        }
        if (tweak.length > MAX_TWEAK_LENGTH) {
            throw new IllegalArgumentException("tweak must be at most " + MAX_TWEAK_LENGTH + " bytes");
        }
        return tweak.clone();
    }
}