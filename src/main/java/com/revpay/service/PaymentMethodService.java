package com.revpay.service;

import java.util.List;
import java.util.Scanner;

import com.revpay.dao.AccountDao;
import com.revpay.dao.AccountDaoImpl;
import com.revpay.dao.PaymentMethodDao;
import com.revpay.dao.PaymentMethodDaoImpl;
import com.revpay.dao.TransactionDao;
import com.revpay.dao.TransactionDaoImpl;
import com.revpay.dao.NotificationDao;
import com.revpay.dao.NotificationDaoImpl;
import com.revpay.dao.UserDao;
import com.revpay.dao.UserDaoImpl;

public class PaymentMethodService {

    private Scanner sc = new Scanner(System.in);
    private PaymentMethodDao paymentDao = new PaymentMethodDaoImpl();
    private AccountDao accountDao = new AccountDaoImpl();
    private TransactionDao transactionDao = new TransactionDaoImpl();
    private NotificationDao notificationDao = new NotificationDaoImpl();
    private UserDao userDao = new UserDaoImpl();

    // ===================== MANAGE WALLET & PAYMENTS =====================
    public void manageWalletAndPayments(int userId) {

        while (true) {

            System.out.println("\n--- Manage Wallet & Payments ---");
            System.out.println("1. Manage Cards");
            System.out.println("2. Manage Bank Accounts");
            System.out.println("3. Add Money via Card");
            System.out.println("4. Withdraw to Bank");
            System.out.println("5. Back");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    manageCards(userId);
                    break;

                case 2:
                    manageBanks(userId);
                    break;

                case 3:
                    addMoneyViaCard(userId);
                    break;

                case 4:
                    withdrawToBank(userId);
                    break;

                case 5:
                    return;

                default:
                    System.out.println("Invalid option");
            }
        }
    }

    // ===================== MANAGE CARDS =====================
    public void manageCards(int userId) {

        while (true) {

            System.out.println("\n--- Manage Cards ---");
            System.out.println("1. Add Card");
            System.out.println("2. View Cards");
            System.out.println("3. Set Default Card");
            System.out.println("4. Delete Card");
            System.out.println("5. Block/Unblock Card");
            System.out.println("6. Back");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Card Holder Name: ");
                    String holder = sc.nextLine();

                    System.out.print("Card Number: ");
                    String number = sc.nextLine();

                    System.out.print("Expiry (MM/YY): ");
                    String expiry = sc.nextLine();

                    System.out.print("Card Type (CREDIT/DEBIT): ");
                    String type = sc.nextLine();

                    System.out.print("CVV (Not Stored): ");
                    sc.nextLine();

                    paymentDao.addCard(userId, holder, number, expiry, type);

                    notificationDao.addNotification(
                            userId,
                            "New card added successfully",
                            "CARD"
                    );
                    break;

                case 2:
                    List<String> cards = paymentDao.viewCards(userId);
                    cards.forEach(System.out::println);
                    break;

                case 3:
                    System.out.print("Enter Card ID: ");
                    int defaultId = sc.nextInt();
                    sc.nextLine();

                    paymentDao.setDefaultCard(userId, defaultId);

                    notificationDao.addNotification(
                            userId,
                            "Default card updated",
                            "CARD"
                    );
                    break;

                case 4:
                    System.out.print("Enter Card ID to delete: ");
                    int deleteId = sc.nextInt();
                    sc.nextLine();

                    paymentDao.deleteCard(userId, deleteId);

                    notificationDao.addNotification(
                            userId,
                            "Card deleted",
                            "CARD"
                    );
                    break;

                case 5:
                    System.out.print("Enter Card ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter Status (ACTIVE/BLOCKED): ");
                    String status = sc.nextLine();

                    paymentDao.updateCardStatus(userId, id, status);
                    break;

                case 6:
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    // ===================== ADD MONEY VIA CARD =====================
    public void addMoneyViaCard(int userId) {

        List<String> cards = paymentDao.viewCards(userId);

        if (cards.isEmpty()) {
            System.out.println("❌ No cards available.");
            return;
        }

        System.out.println("\nSelect Card ID:");
        cards.forEach(System.out::println);

        System.out.print("Enter Card ID: ");
        int cardId = sc.nextInt();
        sc.nextLine();

        if (!paymentDao.isCardActive(userId, cardId)) {
            System.out.println("❌ Invalid or inactive card.");
            return;
        }

        System.out.print("Enter amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        accountDao.addBalance(userId, amount);

        transactionDao.addTransaction(
                userId,
                amount,
                "ADD_MONEY (Card ID: " + cardId + ")"
        );

        notificationDao.addNotification(
                userId,
                "₹" + amount + " added via Card ID " + cardId,
                "TRANSACTION"
        );

        System.out.println("✅ Money added successfully");
    }

    // ===================== WITHDRAW TO BANK =====================
    public void withdrawToBank(int userId) {

        List<String> banks = paymentDao.viewBankAccounts(userId);

        if (banks.isEmpty()) {
            System.out.println("❌ No bank accounts available.");
            return;
        }

        System.out.println("\nSelect Bank ID:");
        banks.forEach(System.out::println);

        System.out.print("Enter Bank ID: ");
        int bankId = sc.nextInt();
        sc.nextLine();

        if (!paymentDao.isBankActive(userId, bankId)) {
            System.out.println("❌ Invalid or inactive bank.");
            return;
        }

        System.out.print("Enter amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        double balance = accountDao.getBalance(userId);

        if (balance < amount) {
            System.out.println("❌ Insufficient balance");
            return;
        }

        System.out.print("Enter Transaction PIN: ");
        String pin = sc.nextLine();

        if (!userDao.verifyTransactionPin(userId, pin)) {
            System.out.println("❌ Invalid Transaction PIN");
            return;
        }

        accountDao.addBalance(userId, -amount);

        transactionDao.addTransaction(
                userId,
                amount,
                "WITHDRAW (Bank ID: " + bankId + ")"
        );

        notificationDao.addNotification(
                userId,
                "₹" + amount + " withdrawn to Bank ID " + bankId,
                "TRANSACTION"
        );

        double updatedBalance = accountDao.getBalance(userId);

        if (updatedBalance < 1000) {
            notificationDao.addNotification(
                    userId,
                    "Low wallet balance! Current balance: ₹" + updatedBalance,
                    "ALERT"
            );
        }

        System.out.println("✅ Withdrawal successful");
    }

    // ===================== MANAGE BANKS =====================
    public void manageBanks(int userId) {

        while (true) {

            System.out.println("\n--- Manage Bank Accounts ---");
            System.out.println("1. Add Bank");
            System.out.println("2. View Banks");
            System.out.println("3. Set Default Bank");
            System.out.println("4. Delete Bank");
            System.out.println("5. Back");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Bank Name: ");
                    String bank = sc.nextLine();

                    System.out.print("Account Number: ");
                    String acc = sc.nextLine();

                    paymentDao.addBankAccount(userId, bank, acc);

                    notificationDao.addNotification(
                            userId,
                            "Bank account added",
                            "BANK"
                    );
                    break;

                case 2:
                    List<String> banks = paymentDao.viewBankAccounts(userId);
                    banks.forEach(System.out::println);
                    break;

                case 3:
                    System.out.print("Enter Bank ID: ");
                    int defaultId = sc.nextInt();
                    sc.nextLine();

                    paymentDao.setDefaultBank(userId, defaultId);
                    break;

                case 4:
                    System.out.print("Enter Bank ID: ");
                    int deleteId = sc.nextInt();
                    sc.nextLine();

                    paymentDao.deleteBankAccount(userId, deleteId);

                    notificationDao.addNotification(
                            userId,
                            "Bank account deleted",
                            "BANK"
                    );
                    break;

                case 5:
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}
