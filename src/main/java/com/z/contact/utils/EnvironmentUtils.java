package com.z.contact.utils;

import java.util.Optional;

public class EnvironmentUtils {
    public static Optional<String> getEnvironmentVariable(String key) {
        return Optional.ofNullable(System.getenv(key));
    }
}
