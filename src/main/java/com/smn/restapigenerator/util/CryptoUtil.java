package com.smn.restapigenerator.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class CryptoUtil {

   // Must be constant to keep the output deterministic.
    // Ideally store this in config/secret manager, not hard-coded.
    private static final byte[] FIXED_SALT =
            "replace-with-a-long-random-app-secret-salt".getBytes(StandardCharsets.UTF_8);

    // Tune for your environment (higher = slower = better against brute force)
    private static final int ITERATIONS = 210_000;

    // 256-bit derived key
    private static final int KEY_LENGTH_BITS = 256;

    /**
     * Deterministically encrypts a password (same password => same encrypted output every time).
     * Returns Base64 (URL-safe) text.
     */
    public static String encrypt(String password) {
        if (password == null) throw new IllegalArgumentException("password cannot be null");
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), FIXED_SALT, ITERATIONS, KEY_LENGTH_BITS);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] derived = skf.generateSecret(spec).getEncoded();
            // URL-safe Base64 avoids '+' and '/' if you want to store in URLs/logs safely
            return Base64.getUrlEncoder().withoutPadding().encodeToString(derived);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Hashing failed", e);
        }
    }
}
