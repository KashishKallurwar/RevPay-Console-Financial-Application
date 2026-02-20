package com.revpay.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.revpay.dao.AccountDao;
import com.revpay.dao.AccountDaoImpl;

public class AccountDaoTest {

	@Test
	public void testGetBalanceDoesNotReturnNegative() {

	    int testUserId = 1;

	    AccountDao dao = new AccountDaoImpl();

	    double balance = dao.getBalance(testUserId);

	    assertTrue(balance >= 0, "Balance should not be negative");
	}
}
