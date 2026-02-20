package com.revpay.dao;

public interface RequestMoneyDao {

    void createRequest(int requesterId, int requestedId, double amount);

    void viewRequests(int userId);

    void updateRequestStatus(int requestId, String status);

    RequestDetails getRequestById(int requestId);
    
    void cancelRequest(int requestId, int userId);

}
