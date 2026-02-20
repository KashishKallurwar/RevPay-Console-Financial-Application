package com.revpay.model;

public class MoneyRequest {

    private int requestId;
    private int requesterId;
    private int requestedId;
    private double amount;
    private String status;

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public int getRequestedId() {
        return requestedId;
    }

    public void setRequestedId(int requestedId) {
        this.requestedId = requestedId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
