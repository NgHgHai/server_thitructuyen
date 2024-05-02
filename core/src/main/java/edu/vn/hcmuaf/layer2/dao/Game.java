package edu.vn.hcmuaf.layer2.dao;

public class Game {

    public static class StringUtils {
        public static String defaultIfNull(String str) {
            return str == null ? "" : str;
        }

        public static boolean isNullOrEmpty(String str) {
            return str == null || str.isEmpty();
        }
    }
}
