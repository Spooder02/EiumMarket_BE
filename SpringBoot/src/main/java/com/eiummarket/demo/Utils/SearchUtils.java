package com.eiummarket.demo.Utils;

public class SearchUtils {
    public static String sanitize(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return null;
        // 길이 상한(예: 100자)으로 과도한 검색 방지
        return trimmed.length() > 100 ? trimmed.substring(0, 100) : trimmed;
    }

    // LIKE 검색용 이스케이프
    public static String escapeLike(String s) {
        return s.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
