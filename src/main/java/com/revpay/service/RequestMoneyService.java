package com.revpay.service;

import java.util.Scanner;

import com.revpay.dao.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestMoneyService {

    private static final Logger logger = LogManager.getLogger(RequestMoneyService.class);

    private Scanner sc = new Scanner(System.in);
    private RequestMoneyDao requestDao = new RequestMoneyDaoImpl();
    private AccountDao accountDao = new AccountDaoImpl();
    private TransactionDao transactionDao = new TransactionDaoImpl();
    private NotificationDao notificationDao = new NotificationDaoImpl();
    private UserDao userDao = new UserDaoImpl();

    // ================= CREATE REQUEST =================
    public void requestMoney(int requesterId) {

        System.out.print("Enter user email to request money from: ");
        String email = sc.nextLine();

        int receiverId = userDao.getUserIdByEmail(email);

        if (receiverId == -1) {
            System.out.println("❌ User not found");
            return;
        }

        if (receiverId == requesterId) {
            System.out.println("❌ Cannot request money from yourself");
            return;
        }

        System.out.print("Enter amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        requestDao.createRequest(requesterId, receiverId, amount);

        // 🔔 Notification to receiver
        notificationDao.addNotification(
                receiverId,
                "You have received a new money request of ₹" + amount,
                "REQUEST"
        );

        System.out.println("✅ Money request sent successfully");
        logger.info("Request created by {} to {}", requesterId, receiverId);
    }


    // ================= VIEW & HANDLE REQUESTS =================
    public void viewAndHandleRequests(int userId) {

        requestDao.viewRequests(userId);

        System.out.print("\nEnter Request ID to respond (0 to exit): ");
        int requestId = sc.nextInt();
        sc.nextLine();

        if (requestId == 0) return;

        RequestDetails req = requestDao.getRequestById(requestId);

        if (req == null || !"UNPAID".equals(req.status)) {
            System.out.println("❌ Invalid request");
            return;
        }

        System.out.println("1. Accept");
        System.out.println("2. Reject");
        System.out.println("3. Cancel");
        System.out.println("4. Back");
        System.out.print("Choose option: ");

        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {

            double balance = accountDao.getBalance(userId);

            if (balance < req.amount) {
                System.out.println("❌ Insufficient balance");

                notificationDao.addNotification(
                        userId,
                        "Low wallet balance! Cannot accept request of ₹" + req.amount,
                        "ALERT"
                );
                return;
            }

            accountDao.deductBalance(userId, req.amount);
            accountDao.addBalance(req.requesterId, req.amount);

            transactionDao.transferTransaction(
                    userId,
                    req.requesterId,
                    req.amount,
                    "Request accepted"
            );

            requestDao.updateRequestStatus(requestId, "ACCEPTED");

            // 🔔 Notify requester
            notificationDao.addNotification(
                    req.requesterId,
                    "Your money request of ₹" + req.amount + " has been accepted",
                    "REQUEST"
            );

            System.out.println("✅ Request accepted");
            logger.info("Request {} accepted by {}", requestId, userId);

        } else if (choice == 2) {

            requestDao.updateRequestStatus(requestId, "REJECTED");

            notificationDao.addNotification(
                    req.requesterId,
                    "Your money request of ₹" + req.amount + " has been rejected",
                    "REQUEST"
            );

            System.out.println("❌ Request rejected");
            logger.info("Request {} rejected by {}", requestId, userId);

        } else if (choice == 3) {

            if (req.requesterId != userId) {
                System.out.println("❌ Only requester can cancel this request");
                return;
            }

            requestDao.updateRequestStatus(requestId, "CANCELLED");

            notificationDao.addNotification(
                    req.receiver_id,
                    "Money request of ₹" + req.amount + " has been cancelled",
                    "REQUEST"
            );

            System.out.println("⚠ Request cancelled successfully");
            logger.info("Request {} cancelled by {}", requestId, userId);

        } else {
            System.out.println("Invalid option");
        }
    }
}
