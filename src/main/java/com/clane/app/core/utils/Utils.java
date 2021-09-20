package com.clane.app.core.utils;

import org.springframework.util.DigestUtils;

public class Utils {

    public static String hash(String principal) {
        return DigestUtils.md5DigestAsHex(principal.getBytes());
    }

    public static String generateRandomString() {
        return String.valueOf(System.currentTimeMillis());
    }
}
