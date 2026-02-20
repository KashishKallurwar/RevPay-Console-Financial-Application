package com.revpay.dao;

import java.util.List;
import com.revpay.model.Notification;

public interface NotificationDao {

    void addNotification(int userId, String message, String type);

    List<Notification> getNotificationsByUser(int userId);

    void markAsRead(int notificationId);

    void showNotifications(int userId);
}
