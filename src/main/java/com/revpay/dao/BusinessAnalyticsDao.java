package com.revpay.dao;

public interface BusinessAnalyticsDao {

    void showTransactionSummary(int businessId);

    void showRevenueReport(int businessId);

    void showOutstandingInvoices(int businessId);

    void showPaymentTrends(int businessId);

    void showTopCustomers(int businessId);
}