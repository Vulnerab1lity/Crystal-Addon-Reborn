package com.crystaldevs.crystal.utils;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_!@#$%^&*()-+=<>?";

    public static String generateRandomPassword(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be greater than zero.");
        }

        if (length > 100) {
            throw new IllegalArgumentException("Password length must be smaller than 100.");
        }

        return IntStream.range(0, length)
                .mapToObj(i -> String.valueOf(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length()))))
                .collect(Collectors.joining());
    }
}