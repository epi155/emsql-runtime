package io.github.epi155.emsql.fpe;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

final class Ff1Core {

    private static final int ROUNDS = 10;
    private static final int BLOCK_SIZE = 16;

    private Ff1Core() {
    }

    static int[] encrypt(int[] x, int radix, byte[] key, byte[] tweak) {
        return crypt(x, radix, key, tweak, false);
    }

    static int[] decrypt(int[] x, int radix, byte[] key, byte[] tweak) {
        return crypt(x, radix, key, tweak, true);
    }

    private static int[] crypt(int[] x, int radix, byte[] key, byte[] tweak, boolean decrypt) {
        int n = x.length;
        int u = n / 2;
        int v = n - u;
        int b = bytesForRadixPower(radix, v);
        int d = 4 * ((b + 3) / 4) + 4;

        int[] a = Arrays.copyOfRange(x, 0, u);
        int[] c = Arrays.copyOfRange(x, u, n);

        byte[] p = buildP(radix, u, n, tweak.length);
        Cipher cipher = newCipher(key);

        int from = decrypt ? ROUNDS - 1 : 0;
        int to = decrypt ? -1 : ROUNDS;
        int step = decrypt ? -1 : 1;

        for (int i = from; i != to; i += step) {
            int[] qSource = decrypt ? a : c;
            byte[] q = buildQ(tweak, i, num(qSource, radix), b);
            byte[] r = prf(cipher, p, q);
            byte[] s = Arrays.copyOf(r, d);
            BigInteger y = new BigInteger(1, s);
            int m = (i % 2 == 0) ? u : v;
            BigInteger modulus = BigInteger.valueOf(radix).pow(m);
            BigInteger value;
            if (decrypt) {
                value = num(c, radix).subtract(y).mod(modulus);
            } else {
                value = num(a, radix).add(y).mod(modulus);
            }
            int[] round = str(value, radix, m);
            if (decrypt) {
                c = a;
                a = round;
            } else {
                a = c;
                c = round;
            }
        }

        int[] out = new int[a.length + c.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(c, 0, out, a.length, c.length);
        return out;
    }

    static int bytesForRadixPower(int radix, int exponent) {
        BigInteger max = BigInteger.valueOf(radix).pow(exponent).subtract(BigInteger.ONE);
        return (max.bitLength() + 7) / 8;
    }

    static byte[] buildP(int radix, int u, int n, int t) {
        byte[] p = new byte[BLOCK_SIZE];
        p[0] = 1;
        p[1] = 2;
        p[2] = 1;
        p[3] = (byte) (radix >>> 16);
        p[4] = (byte) (radix >>> 8);
        p[5] = (byte) radix;
        p[6] = 10;
        p[7] = (byte) u;
        p[8] = (byte) (n >>> 24);
        p[9] = (byte) (n >>> 16);
        p[10] = (byte) (n >>> 8);
        p[11] = (byte) n;
        p[12] = (byte) (t >>> 24);
        p[13] = (byte) (t >>> 16);
        p[14] = (byte) (t >>> 8);
        p[15] = (byte) t;
        return p;
    }

    static byte[] buildQ(byte[] tweak, int i, BigInteger sideNum, int b) {
        int t = tweak.length;
        int padding = (((-t - b - 1) % 16) + 16) % 16;
        byte[] q = new byte[t + padding + 1 + b];
        System.arraycopy(tweak, 0, q, 0, t);
        q[t + padding] = (byte) i;
        byte[] encoded = toFixedBytes(sideNum, b);
        System.arraycopy(encoded, 0, q, t + padding + 1, b);
        return q;
    }

    static byte[] prf(Cipher cipher, byte[] p, byte[] q) {
        byte[] pq = new byte[p.length + q.length];
        System.arraycopy(p, 0, pq, 0, p.length);
        System.arraycopy(q, 0, pq, p.length, q.length);
        byte[] r = new byte[BLOCK_SIZE];
        byte[] block = new byte[BLOCK_SIZE];
        for (int offset = 0; offset < pq.length; offset += BLOCK_SIZE) {
            for (int k = 0; k < BLOCK_SIZE; k++) {
                block[k] = (byte) (r[k] ^ pq[offset + k]);
            }
            r = aes(cipher, block);
        }
        return r;
    }

    static Cipher newCipher(byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            return cipher;
        } catch (GeneralSecurityException e) {
            throw new FpeException("Cannot initialize AES", e);
        }
    }

    static BigInteger num(int[] digits, int radix) {
        BigInteger value = BigInteger.ZERO;
        for (int d : digits) {
            value = value.multiply(BigInteger.valueOf(radix)).add(BigInteger.valueOf(d));
        }
        return value;
    }

    static int[] str(BigInteger value, int radix, int length) {
        BigInteger base = BigInteger.valueOf(radix);
        int[] out = new int[length];
        BigInteger x = value;
        for (int i = length - 1; i >= 0; i--) {
            BigInteger[] qr = x.divideAndRemainder(base);
            out[i] = qr[1].intValue();
            x = qr[0];
        }
        return out;
    }

    private static byte[] aes(Cipher cipher, byte[] block) {
        try {
            return cipher.doFinal(block);
        } catch (GeneralSecurityException e) {
            throw new FpeException("AES block encryption failed", e);
        }
    }

    private static byte[] toFixedBytes(BigInteger value, int length) {
        byte[] raw = value.toByteArray();
        byte[] out = new byte[length];
        int copy = Math.min(length, raw.length);
        System.arraycopy(raw, raw.length - copy, out, length - copy, copy);
        return out;
    }
}