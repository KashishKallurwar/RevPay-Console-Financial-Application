package com.revpay.dao;

public interface InvoiceDao {

    int  createEmptyInvoice(int businessId, int customerId);

    void addInvoiceItem(int invoiceId, String desc, int qty, double unitPrice, double total);

    void finalizeInvoice(int invoiceId, double totalAmount, String paymentTerms);

    void viewInvoicesByBusiness(int businessId);

    void viewInvoicesByStatus(int businessId);

    void viewInvoicesByCustomer(int customerId);

    boolean isInvoiceUnpaid(int invoiceId);

    double getInvoiceAmount(int invoiceId);

    int getBusinessId(int invoiceId);

    void markInvoicePaid(int invoiceId);
    
    
}
