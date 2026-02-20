package com.revpay.service;

import java.util.Scanner;

import com.revpay.dao.TransactionDao;
import com.revpay.dao.TransactionDaoImpl;

public class TransactionHistoryService {

    private TransactionDao transactionDao = new TransactionDaoImpl();
    private Scanner sc = new Scanner(System.in);

    public void showTransactions(int userId) {

        while (true) {

        	System.out.println("\n--- Transaction History ---");
        	System.out.println("1. View All");
        	System.out.println("2. Filter by Type");
        	System.out.println("3. Filter by Date Range");
        	System.out.println("4. Filter by Amount Range");
        	System.out.println("5. Filter by Status");
        	System.out.println("6. Search by Keyword");
        	System.out.println("7. View Summary");
        	System.out.println("8. Export to CSV");
        	System.out.println("9. Back");


            int choice = sc.nextInt();

            switch (choice) {

            case 1:
                transactionDao.viewTransactions(userId);
                break;

            case 2:
                System.out.print("Enter Type (ADD_MONEY / WITHDRAW / SEND_MONEY): ");
                String type = sc.next();
                transactionDao.viewTransactionsByType(userId, type);
                break;

            case 3:
                System.out.print("Enter Start Date (YYYY-MM-DD): ");
                String start = sc.next();
                System.out.print("Enter End Date (YYYY-MM-DD): ");
                String end = sc.next();
                transactionDao.viewByDateRange(userId, start, end);
                break;

            case 4:
                System.out.print("Enter Minimum Amount: ");
                double min = sc.nextDouble();
                System.out.print("Enter Maximum Amount: ");
                double max = sc.nextDouble();
                transactionDao.viewByAmountRange(userId, min, max);
                break;

            case 5:
                System.out.print("Enter Status (SUCCESS / FAILED): ");
                String status = sc.next();
                transactionDao.viewByStatus(userId, status);
                break;

            case 6:
                System.out.print("Enter keyword to search: ");
                String keyword = sc.next();
                transactionDao.searchByKeyword(userId, keyword);
                break;

            case 7:
                transactionDao.viewSummary(userId);
                break;

            case 8:
                transactionDao.exportToCSV(userId);
                break;

            case 9:
                return;

            default:
                System.out.println("Invalid option");
        }

        }
    }
}
