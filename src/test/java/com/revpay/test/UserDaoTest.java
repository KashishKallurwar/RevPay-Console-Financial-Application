package com.revpay.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.revpay.dao.UserDao;
import com.revpay.dao.UserDaoImpl;

public class UserDaoTest {
 
   
    @Test
    public void testLoginUserWithReturnId() {

        UserDao dao = new UserDaoImpl();

        int userId = dao.loginUserWithReturnId();

        assertTrue(userId == -1 || userId > 0);
    }

}
