package com.example.iotbackend.util;

import java.util.HashMap;
import java.util.Map;

public class DeviceVocabulary {

    private static final Map<String, String> DEVICE_MAP = new HashMap<>();

    static {

        // ===== ĐÈN =====
        DEVICE_MAP.put("den", "den");
        DEVICE_MAP.put("đèn", "den");
        DEVICE_MAP.put("đen", "den");
        DEVICE_MAP.put("dèn", "den");
        DEVICE_MAP.put("lèn", "den");
        DEVICE_MAP.put("dền", "den");
        DEVICE_MAP.put("bong den", "den");
        DEVICE_MAP.put("bóng đèn", "den");

        // ===== QUẠT =====
        DEVICE_MAP.put("quat", "quat");
        DEVICE_MAP.put("quạt", "quat");

        // ===== TIVI =====
        DEVICE_MAP.put("tivi", "tivi");
        DEVICE_MAP.put("tv", "tivi");
    }

    public static String normalizeDeviceName(String input) {

        input = TextNormalizer.normalize(input);

        // match nguyên cụm
        if (DEVICE_MAP.containsKey(input)) {
            return DEVICE_MAP.get(input);
        }

        // match từng từ trong câu (bật đèn -> den)
        String[] words = input.split(" ");
        for (String w : words) {
            if (DEVICE_MAP.containsKey(w)) {
                return DEVICE_MAP.get(w);
            }
        }

        // fallback fuzzy nhẹ (sai 1 ký tự vẫn bắt được)
        for (String w : words) {
            for (String key : DEVICE_MAP.keySet()) {
                if (levenshtein(w, key) <= 1) {
                    return DEVICE_MAP.get(key);
                }
            }
        }

        // không match thì trả về raw
        return input;
    }

    private static int levenshtein(String a, String b) {

        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {

                if (i == 0) dp[i][j] = j;
                else if (j == 0) dp[i][j] = i;
                else {
                    dp[i][j] = Math.min(
                            Math.min(
                                    dp[i - 1][j] + 1,
                                    dp[i][j - 1] + 1
                            ),
                            dp[i - 1][j - 1] +
                                    (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1)
                    );
                }
            }
        }

        return dp[a.length()][b.length()];
    }
}