package com.revpay.service;

import java.util.Scanner;
import java.util.List;

import com.revpay.dao.*;
import com.revpay.util.Session;

public class InvoiceService {

    private Scanner sc = new Scanner(System.in);

    private InvoiceDao invoiceDao = new InvoiceDaoImpl();
    private UserDao userDao = new UserDaoImpl();
    private AccountDao accountDao = new AccountDaoImpl();
    private TransactionDao transactionDao = new TransactionDaoImpl();
    private PaymentMethodDao paymentDao = new PaymentMethodDaoImpl();
    private NotificationDao notificationDao = new NotificationDaoImpl();

    // ================= MAIN INVOICE MENU =================
    public void manageInvoices(int businessId) {

        while (true) {

            System.out.println("\n===== INVOICE MANAGEMENT =====");
            System.out.println("1. Create Invoice");
            System.out.println("2. View Invoices I Created");
            System.out.println("3. View Invoices To Pay");
            System.out.println("4. Track Unpaid Invoices");
            System.out.println("5. Pay Invoice");
            System.out.println("6. Back");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    createInvoice(businessId);
                    break;

                case 2:
                    invoiceDao.viewInvoicesByBusiness(businessId);
                    break;

                case 3:
                    invoiceDao.viewInvoicesByCustomer(businessId);
                    break;

                case 4:
                    invoiceDao.viewInvoicesByStatus(businessId);
                    break;

                case 5:
                    payInvoice(businessId);
                    break;

                case 6:
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }

 // ================= CREATE INVOICE =================
    public void createInvoice(int businessId) {

        System.out.print("Enter customer email: ");
        String email = sc.nextLine();

        int customerId = userDao.getUserIdByEmail(email);

        if (customerId == -1) {
            System.out.println("❌ Customer not found");
            return;
        }

        // 1️⃣ Create empty invoice first
        int invoiceId = invoiceDao.createEmptyInvoice(businessId, customerId);

        if (invoiceId == -1) {
            System.out.println("❌ Invoice creation failed");
            return;
        }

        double totalAmount = 0;

        // 2️⃣ Add itemized details
        while (true) {

            System.out.print("Enter item description: ");
            String desc = sc.nextLine();

            System.out.print("Enter quantity: ");
            int qty = sc.nextInt();

            System.out.print("Enter unit price: ");
            double price = sc.nextDouble();
            sc.nextLine();

            double itemTotal = qty * price;
            totalAmount += itemTotal;

            invoiceDao.addInvoiceItem(invoiceId, desc, qty, price, itemTotal);

            System.out.print("Add more items? (Y/N): ");
            String more = sc.nextLine();

            if (!more.equalsIgnoreCase("Y")) break;
        }

        // 3️⃣ Enter payment terms
        System.out.print("Enter payment terms (e.g. 7 Days / 30 Days): ");
        String terms = sc.nextLine();

        // 4️⃣ Finalize invoice
        invoiceDao.finalizeInvoice(invoiceId, totalAmount, terms);

        // 5️⃣ Send notification
        notificationDao.addNotification(
                customerId,
                "New invoice received. Amount: ₹" + totalAmount,
                "INVOICE"
        );

        System.out.println("✅ Invoice created successfully. Invoice ID: " + invoiceId);
    }

    // ================= CUSTOMER SIDE PAY INVOICE =================
    public void payInvoice(int customerId) {

        invoiceDao.viewInvoicesByCustomer(customerId);

        System.out.print("Enter Invoice ID to pay: ");
        int invoiceId = sc.nextInt();
        sc.nextLine();

        if (!invoiceDao.isInvoiceUnpaid(invoiceId)) {
            System.out.println("❌ Invoice already paid or invalid");
            return;
        }

        double amount = invoiceDao.getInvoiceAmount(invoiceId);

        System.out.println("1. Pay via Wallet");
        System.out.println("2. Pay via Card");

        int option = sc.nextInt();
        sc.nextLine();

        if (option == 1) {

            double balance = accountDao.getBalance(customerId);

            if (balance < amount) {
                System.out.println("❌ Insufficient wallet balance");
                return;
            }

            accountDao.addBalance(customerId, -amount);

        } else if (option == 2) {

            List<String> cards = paymentDao.viewCards(customerId);

            if (cards.isEmpty()) {
                System.out.println("❌ No cards available");
                return;
            }

            cards.forEach(System.out::println);

            System.out.print("Enter Card ID: ");
            int cardId = sc.nextInt();
            sc.nextLine();

            if (!paymentDao.isCardActive(customerId, cardId)) {
                System.out.println("❌ Invalid card");
                return;
            }

        } else {
            System.out.println("Invalid option");
            return;
        }

        int businessId = invoiceDao.getBusinessId(invoiceId);

        accountDao.addBalance(businessId, amount);

        transactionDao.transferTransaction(
                customerId,
                businessId,
                amount,
                "Invoice Payment"
        );

        invoiceDao.markInvoicePaid(invoiceId);

        notificationDao.addNotification(
                businessId,
                "Invoice " + invoiceId + " paid successfully",
                "INVOICE"
        );
        

        System.out.println("✅ Invoice paid successfully");
    }
}
