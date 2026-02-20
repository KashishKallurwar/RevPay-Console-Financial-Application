package com.revpay.model;

import java.sql.Timestamp;

public class Notification {

    private int notificationId;
    private int userId;
    private String message;
    private String type;
    private String isRead;
    private Timestamp createdAt;

    // ===== Getters & Setters =====

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // 🔥 THIS WAS MISSING
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
