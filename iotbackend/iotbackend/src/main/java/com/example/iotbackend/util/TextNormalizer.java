package com.example.iotbackend.util;

import java.text.Normalizer;

public class TextNormalizer {

    public static String normalize(String input) {

        if (input == null) return "";

        // lowercase
        input = input.toLowerCase();

        // bỏ dấu tiếng Việt
        input = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // chuẩn hóa 1 số ký tự hay lỗi STT
        input = input
                .replace("đ", "d")
                .replaceAll("[^a-z0-9\\s]", " ");

        // gộp khoảng trắng
        input = input.replaceAll("\\s+", " ").trim();

        return input;
    }
}