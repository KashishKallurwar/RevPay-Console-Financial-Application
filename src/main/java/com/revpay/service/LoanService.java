package com.revpay.service;

import java.util.Scanner;

import com.revpay.dao.LoanDao;
import com.revpay.dao.LoanDaoImpl;

public class LoanService {

    private Scanner sc = new Scanner(System.in);
    private LoanDao loanDao = new LoanDaoImpl();

    // ================= MAIN MENU =================
    public void manageLoans(int userId) {

        while (true) {

            System.out.println("\n===== LOAN MANAGEMENT =====");
            System.out.println("1. Apply for Loan");
            System.out.println("2. View My Loans");
            System.out.println("3. View Loan Status");
            System.out.println("4. Repay Loan");
            System.out.println("5. View Repayment History");
            System.out.println("6. Back");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    applyLoan(userId);
                    break;

                case 2:
                    loanDao.viewLoansByUser(userId);
                    break;

                case 3:
                    loanDao.viewLoanStatus(userId);
                    break;

                case 4:
                    repayLoan(userId);
                    break;

                case 5:
                    viewRepaymentHistory(userId);
                    break;

                case 6:
                    return;

                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }

    // ================= APPLY LOAN =================
    private void applyLoan(int userId) {

        System.out.print("Enter Loan Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        System.out.print("Enter Purpose of Loan: ");
        String purpose = sc.nextLine();

        System.out.print("Enter Annual Revenue: ");
        double revenue = sc.nextDouble();

        System.out.print("Enter Credit Score: ");
        int creditScore = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Supporting Document Name: ");
        String document = sc.nextLine();

        loanDao.applyLoan(
                userId,
                amount,
                purpose,
                revenue,
                creditScore,
                document
        );
    }

    // ================= REPAY LOAN =================
    private void repayLoan(int userId) {

        loanDao.viewLoansByUser(userId);

        System.out.print("Enter Loan ID to repay: ");
        int loanId = sc.nextInt();

        System.out.print("Enter repayment amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        loanDao.repayLoan(loanId, userId, amount);
    }

    // ================= VIEW REPAYMENT HISTORY =================
    private void viewRepaymentHistory(int userId) {

        System.out.print("Enter Loan ID: ");
        int loanId = sc.nextInt();
        sc.nextLine();

        loanDao.viewRepaymentHistory(loanId, userId);
    }
}
