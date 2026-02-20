package com.revpay.dao;

public interface BusinessDao {
    void registerBusiness(int userId, String name, String type, String taxId, String address);
    
    void saveBusinessDetails(
            int userId,
            String businessName,
            String businessType,
            String taxId,
            String address,
            String verificationDoc
    );
}
