package com.revpay.service;

import java.util.Scanner;

import com.revpay.dao.BusinessAnalyticsDao;
import com.revpay.dao.BusinessAnalyticsDaoImpl;

public class BusinessAnalyticsService {

    private BusinessAnalyticsDao dao = new BusinessAnalyticsDaoImpl();
    private Scanner sc = new Scanner(System.in);

    public void manageAnalytics(int businessId) {

        while (true) {

            System.out.println("\n===== BUSINESS ANALYTICS =====");
            System.out.println("1. Transaction Summary");
            System.out.println("2. Revenue Report");
            System.out.println("3. Outstanding Invoices");
            System.out.println("4. Payment Trends");
            System.out.println("5. Top Customers");
            System.out.println("6. Back");

            System.out.print("Choose option: ");
            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    dao.showTransactionSummary(businessId);
                    break;

                case 2:
                    dao.showRevenueReport(businessId);
                    break;

                case 3:
                    dao.showOutstandingInvoices(businessId);
                    break;

                case 4:
                    dao.showPaymentTrends(businessId);
                    break;

                case 5:
                    dao.showTopCustomers(businessId);
                    break;

                case 6:
                    return;

                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }
}