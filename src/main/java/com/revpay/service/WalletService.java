package com.revpay.service;

import java.util.Scanner;

import com.revpay.dao.AccountDao;
import com.revpay.dao.AccountDaoImpl;

import com.revpay.dao.WalletDao;
import com.revpay.dao.WalletDaoImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WalletService {
	  private static final Logger logger = LogManager.getLogger(WalletService.class);
	  private WalletDao walletDao = new WalletDaoImpl();
	  public void addMoney(int userId) {

		    Scanner sc = new Scanner(System.in);
		    System.out.print("Enter amount to add: ");
		    double amount = sc.nextDouble();

		    try {
		        AccountDao accountDao = new AccountDaoImpl();
		        accountDao.addBalance(userId, amount);

		        logger.info("₹{} added to wallet for user {}", amount, userId);

		        System.out.println("✅ Money added successfully");

		    } catch (Exception e) {
		        logger.error("Error while adding money for user {}", userId, e);
		        System.out.println("❌ Failed to add money");
		    }
		    
		}
	  public void viewBalance(int userId) {

	        try {
	            double balance = walletDao.getBalance(userId);

	            System.out.println("💰 Your wallet balance is ₹ " + balance);
	            logger.info("Wallet balance checked for user {} : ₹{}", userId, balance);

	        } catch (Exception e) {
	            logger.error("Error while fetching wallet balance for user {}", userId, e);
	            System.out.println("❌ Unable to fetch wallet balance");
	        }
	    }
}

