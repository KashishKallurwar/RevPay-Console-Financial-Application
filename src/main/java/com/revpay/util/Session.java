package com.revpay.util;

public class Session {

    public static int currentUserId = -1;
    public static String accountType;

    private static long lastActivityTime;
    private static final long TIMEOUT_DURATION = 10 * 60 * 1000; // 10 minutes

    // Call on login
    public static void startSession(int userId, String accType) {
        currentUserId = userId;
        accountType = accType;
        refresh();
    }

    // Refresh activity timestamp
    public static void refresh() {
        lastActivityTime = System.currentTimeMillis();
    }

    // Check timeout
    public static boolean isSessionExpired() {
        return System.currentTimeMillis() - lastActivityTime > TIMEOUT_DURATION;
    }

    // Clear session
    public static void clear() {
        currentUserId = -1;
        accountType = null;
        lastActivityTime = 0;
    }
}
