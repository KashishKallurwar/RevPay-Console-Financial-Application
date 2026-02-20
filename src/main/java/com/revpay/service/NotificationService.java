package com.revpay.service;

import java.util.List;
import java.util.Scanner;

import com.revpay.dao.NotificationDao;
import com.revpay.dao.NotificationDaoImpl;
import com.revpay.model.Notification;

public class NotificationService {

    private NotificationDao notificationDao = new NotificationDaoImpl();
    
    private Scanner sc = new Scanner(System.in);

    // ================= SHOW NOTIFICATIONS =================
    public void showNotifications(int userId) {

        List<Notification> notifications =
            notificationDao.getNotificationsByUser(userId);

        if (notifications.isEmpty()) {
            System.out.println("📭 No notifications");
            return;
        }

        System.out.println("----- NOTIFICATIONS -----");

        for (Notification n : notifications) {
            System.out.println(
                "ID: " + n.getNotificationId() +
                " | " + (n.getIsRead().equals("Y") ? "[READ]" : "[NEW]") +
                " | " + n.getMessage() +
                " | " + n.getCreatedAt()
            );
        }

        System.out.print("Enter notification ID to mark as read (0 to skip): ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice != 0) {
            notificationDao.markAsRead(choice);
            System.out.println("✅ Notification marked as read");
        }
    }
}
