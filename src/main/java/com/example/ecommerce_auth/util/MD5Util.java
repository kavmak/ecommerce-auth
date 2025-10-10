package com.example.ecommerce_auth.util;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * Utility for generating MD5 checksums (ETags, response validation, etc.)
 * Do NOT use for passwords.
 */
public class MD5Util {

    public static String generateChecksum(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data.getBytes());
            // Base64 encoding makes it safe for headers
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("Error generating MD5 checksum", e);
        }
    }
}
