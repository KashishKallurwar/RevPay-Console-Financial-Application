package com.revpay.service;

import java.util.*;
import com.revpay.dao.*;
import com.revpay.model.UserSecurityQuestion;
import com.revpay.security.PasswordUtil;

public class PasswordRecoveryService {

    private Scanner sc = new Scanner(System.in);
    private UserDao userDao = new UserDaoImpl();
    private UserSecurityQuestionDao secDao = new UserSecurityQuestionDaoImpl();

    public void forgotPassword() {

        System.out.print("Enter registered Email or Phone: ");
        String input = sc.nextLine();

        int userId = userDao.getUserIdByEmailOrPhone(input);

        if (userId == -1) {
            System.out.println("❌ User not found");
            return;
        }

        // -------- SECURITY QUESTIONS --------
        List<UserSecurityQuestion> questions = secDao.getQuestionsByUserId(userId);
        List<String> answers = new ArrayList<>();
        
        if (questions.size() != 1) {
            System.out.println("❌ Security question not properly set.");
            return;
        }

        for (UserSecurityQuestion q : questions) {
            System.out.println("Question: " + q.getQuestion());
            System.out.print("Answer: ");
            answers.add(sc.nextLine());
        }

        if (!secDao.validateAnswers(userId, answers)) {
            System.out.println("❌ Security answers incorrect");
            return;
        }

        // -------- SECURITY CODE --------
        System.out.print("Enter Security Code: ");
        String code = sc.nextLine();

        String storedHash = userDao.getSecurityCodeHash(userId);
        if (storedHash == null || storedHash.isEmpty()) {
            System.out.println("❌ Security code not found. Please re-register.");
            return;
        }

        if (!PasswordUtil.verifyPassword(code, storedHash)) {
            System.out.println("❌ Invalid security code");
            return;
        }

        // -------- RESET PASSWORD --------
        System.out.print("Enter New Password: ");
        String newPassword = sc.nextLine();

        userDao.updatePassword(userId, PasswordUtil.hashPassword(newPassword));
    }
}
