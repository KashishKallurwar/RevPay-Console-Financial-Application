package com.revpay.main;

import java.util.Scanner;

import com.revpay.dao.UserDao;
import com.revpay.dao.UserDaoImpl;
import com.revpay.service.*;
import com.revpay.util.Session;
import com.revpay.service.PasswordRecoveryService;
import com.revpay.service.PaymentMethodService;


public class MainApp {

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        UserDao userDao = new UserDaoImpl();

        while (true) {
            System.out.println("\n===== REV PAY APPLICATION =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Forgot Password");
            System.out.println("4. Exit");

            System.out.print("Choose option: ");

            int choice = sc.nextInt();
            sc.nextLine();
            PasswordRecoveryService recoveryService = new PasswordRecoveryService();
           

            switch (choice) {

                case 1:
                    userDao.registerUser();
                    break;

                case 2:
                    int userId = userDao.loginUserWithReturnId();
                    if (userId != -1) {

                        if ("PERSONAL".equals(Session.accountType)) {
                            showPersonalDashboard();
                        } else if ("BUSINESS".equals(Session.accountType)) {
                            showBusinessDashboard();
                        } else {
                            System.out.println("❌ Invalid account type. Please login again.");
                        }
                    }
                    break;
                   
                case 3:
                	 recoveryService.forgotPassword();
                    break;

                case 4:
                    System.out.println("Thank you for using RevPay!");
                    System.exit(0);

                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }

    // ================= PERSONAL DASHBOARD =================

    private static void showPersonalDashboard() {

        WalletService walletService = new WalletService();
        TransferService transferService = new TransferService();
        RequestMoneyService requestService = new RequestMoneyService();
        TransactionHistoryService historyService = new TransactionHistoryService();
        LoanService loanService = new LoanService();
        PasswordChangeService passwordChangeService = new PasswordChangeService();
        PaymentMethodService paymentMethodService = new PaymentMethodService();
        NotificationService notificationService = new NotificationService();



        while (true) {
        	if (Session.isSessionExpired()) {
        	    System.out.println("⏱️ Session expired. Please login again.");
        	    Session.clear();
        	    return;
        	}
        	Session.refresh();

            System.out.println("\n===== PERSONAL USER DASHBOARD =====");
            System.out.println("1. Add Money to Wallet");
            System.out.println("2. Wallet Balance");
            System.out.println("3. Send Money");
            System.out.println("4. Request Money");
            System.out.println("5. View & Handle Requests");
            System.out.println("6. View Transaction History");
            System.out.println("7. Manage Wallets & Payments");
            System.out.println("8. Notifications");
            System.out.println("9. Logout");
            System.out.println("10. Change Password");

            System.out.print("Choose option: ");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    walletService.addMoney(Session.currentUserId);
                    break;

                case 2:
                    walletService.viewBalance(Session.currentUserId);
                    break;

                case 3:
                    transferService.sendMoney(Session.currentUserId);
                    break;

                case 4:
                    requestService.requestMoney(Session.currentUserId);
                    break;

                case 5:
                    requestService.viewAndHandleRequests(Session.currentUserId);
                    break;

                case 6:
                    historyService.showTransactions(Session.currentUserId);
                    break;
                
                case 7:
                    paymentMethodService.manageWalletAndPayments(Session.currentUserId);
                    break;
                    
                case 8:
                    notificationService.showNotifications(Session.currentUserId);
                    break;

                case 9:
                    Session.clear();
                    System.out.println("Logged out successfully");
                    return;
                    
                case 10:
                    passwordChangeService.changePassword();
                    break;


                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }

    // ================= BUSINESS DASHBOARD =================

    private static void showBusinessDashboard() {

        WalletService walletService = new WalletService();
        TransferService transferService = new TransferService();
        RequestMoneyService requestService = new RequestMoneyService();
        TransactionHistoryService historyService = new TransactionHistoryService();
        InvoiceService invoiceService = new InvoiceService();
        LoanService loanService = new LoanService();
        NotificationService notificationService = new NotificationService();
        PasswordChangeService passwordChangeService = new PasswordChangeService();
        PaymentMethodService paymentMethodService = new PaymentMethodService();
        BusinessAnalyticsService analyticsService = new BusinessAnalyticsService();
        
        while (true) {
        	if (Session.isSessionExpired()) {
        	    System.out.println("⏱️ Session expired. Please login again.");
        	    Session.clear();
        	    return;
        	}
        	Session.refresh();

        	System.out.println("\n===== BUSINESS USER DASHBOARD =====");
        	System.out.println("1. Add Money to Wallet");
        	System.out.println("2. Wallet Balance");
        	System.out.println("3. Send Money");
        	System.out.println("4. Request Money");
        	System.out.println("5. View & Handle Requests");
        	System.out.println("6. View Transaction History");
        	System.out.println("7. Invoice Management");
        	System.out.println("8. Loan Management");
        	System.out.println("9. Manage Wallet & Payments");
        	System.out.println("10. Business Analytics");
        	System.out.println("11. Notifications");
        	System.out.println("12. Logout");
        	System.out.println("13. Change Password");


            System.out.print("Choose option: ");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    walletService.addMoney(Session.currentUserId);
                    break;

                case 2:
                    walletService.viewBalance(Session.currentUserId);
                    break;

                case 3:
                    transferService.sendMoney(Session.currentUserId);
                    break;

                case 4:
                    requestService.requestMoney(Session.currentUserId);
                    break;

                case 5:
                    requestService.viewAndHandleRequests(Session.currentUserId);
                    break;

                case 6:
                    historyService.showTransactions(Session.currentUserId);
                    break;

                case 7:
                    invoiceService.manageInvoices(Session.currentUserId);
                    break;
                   
                case 8:
                    loanService.manageLoans(Session.currentUserId);
                    break;

                case 9:
                    paymentMethodService.manageWalletAndPayments(Session.currentUserId);
                    break;
                    
                case 10:
                    analyticsService.manageAnalytics(Session.currentUserId);
                    break;


                case 11:
                    notificationService.showNotifications(Session.currentUserId);
                    break;

                case 12:
                    Session.clear();
                    System.out.println("Logged out successfully");
                    return;

                case 13:
                    passwordChangeService.changePassword();
                    break;

              
                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }
}
