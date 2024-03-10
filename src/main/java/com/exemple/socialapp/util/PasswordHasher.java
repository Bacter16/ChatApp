package com.exemple.socialapp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class PasswordHasher {

    public static String hashPassword(String password) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Add password bytes to digest
            md.update(password.getBytes(StandardCharsets.UTF_8));

            // Get the hashed bytes
            byte[] hashedBytes = md.digest();

            // Convert bytes to hexadecimal representation
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception (e.g., log it or throw a custom exception)
            e.printStackTrace();
            return null;
        }
    }
}
