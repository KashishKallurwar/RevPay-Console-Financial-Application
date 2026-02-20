package com.revpay.service;

import java.util.Scanner;

import com.revpay.dao.AccountDao;
import com.revpay.dao.AccountDaoImpl;
import com.revpay.dao.TransactionDao;
import com.revpay.dao.TransactionDaoImpl;


public class InvoicePaymentService {

    private Scanner sc = new Scanner(System.in);

    public void payInvoice(int payerId) {

        System.out.print("Enter business user id: ");
        int businessId = sc.nextInt();

        System.out.print("Enter invoice amount: ");
        double amount = sc.nextDouble();

        AccountDao accountDao = new AccountDaoImpl();
        TransactionDao transactionDao = new TransactionDaoImpl();

        double balance = accountDao.getBalance(payerId);

        if (balance < amount) {
            System.out.println("❌ Insufficient balance");
            return;
        }

        // 1️⃣ Deduct from payer
        accountDao.deductBalance(payerId, amount);

        // 2️⃣ Add to business
        accountDao.addBalance(businessId, amount);

        // 3️⃣ Record transaction (FIXED)
        transactionDao.transferTransaction(
            payerId,
            businessId,
            amount,
            "Invoice payment"
        );

        System.out.println("✅ Invoice paid successfully");
    }
}
