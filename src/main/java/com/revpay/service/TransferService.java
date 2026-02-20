package com.revpay.service;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revpay.dao.*;

public class TransferService {

    private static final Logger logger = LogManager.getLogger(TransferService.class);

    private Scanner sc = new Scanner(System.in);
    private UserDao userDao = new UserDaoImpl();
    private AccountDao accountDao = new AccountDaoImpl();
    private TransactionDao transactionDao = new TransactionDaoImpl();
    private NotificationDao notificationDao = new NotificationDaoImpl();

    public void sendMoney(int senderId) {

        System.out.println("\n--- Send Money ---");
        System.out.println("Send via:");
        System.out.println("1. Email");
        System.out.println("2. Phone");
        System.out.println("3. User ID");
        System.out.print("Choose option: ");

        int lookupChoice = sc.nextInt();
        sc.nextLine();

        String input = "";
        int receiverId = -1;

        switch (lookupChoice) {
            case 1:
                System.out.print("Enter receiver email: ");
                input = sc.nextLine();
                receiverId = userDao.getUserIdByEmail(input);
                break;

            case 2:
                System.out.print("Enter receiver phone: ");
                input = sc.nextLine();
                receiverId = userDao.getUserIdByEmailOrPhone(input);
                break;

            case 3:
                System.out.print("Enter receiver user ID: ");
                receiverId = sc.nextInt();
                sc.nextLine();
                break;

            default:
                System.out.println("❌ Invalid option");
                return;
        }

        if (receiverId == -1) {
            System.out.println("❌ Receiver not found");
            return;
        }

        if (receiverId == senderId) {
            System.out.println("❌ Cannot send money to yourself");
            return;
        }

        System.out.print("Enter amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        if (amount <= 0) {
            System.out.println("❌ Invalid amount");
            return;
        }

        System.out.print("Enter note (optional): ");
        String note = sc.nextLine();

        if (note == null || note.trim().isEmpty()) {
            note = "Money sent";
        }

        double senderBalance = accountDao.getBalance(senderId);

        if (senderBalance < amount) {
            System.out.println("❌ Insufficient balance");
            return;
        }

        // 🔐 Transaction PIN Verification
        System.out.print("Enter Transaction PIN: ");
        String pin = sc.nextLine();

        if (!userDao.verifyTransactionPin(senderId, pin)) {
            System.out.println("❌ Invalid Transaction PIN");
            return;
        }

        // 💰 Perform transfer
        accountDao.addBalance(senderId, -amount);
        accountDao.addBalance(receiverId, amount);

        transactionDao.transferTransaction(
                senderId,
                receiverId,
                amount,
                note
        );

        String senderEmail = userDao.getEmailByUserId(senderId);

        // 🔔 Notifications
        notificationDao.addNotification(
                receiverId,
                "₹" + amount + " received from " + senderEmail,
                "TRANSACTION"
        );

        notificationDao.addNotification(
                senderId,
                "₹" + amount + " sent successfully",
                "TRANSACTION"
        );

        System.out.println("✅ Money sent successfully");
        logger.info("₹{} transferred from user {} to user {}", amount, senderId, receiverId);
    }
}
