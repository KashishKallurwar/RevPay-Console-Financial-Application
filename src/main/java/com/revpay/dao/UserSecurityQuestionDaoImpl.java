package com.revpay.dao;

import java.sql.*;
import java.util.*;
import com.revpay.model.UserSecurityQuestion;
import com.revpay.util.DBConnection;
import com.revpay.security.PasswordUtil;

public class UserSecurityQuestionDaoImpl implements UserSecurityQuestionDao {

    @Override
    public void saveSecurityQuestions(List<UserSecurityQuestion> questions) {
        String sql = "INSERT INTO user_security_questions (user_id, question, answer_hash) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (UserSecurityQuestion q : questions) {
                ps.setInt(1, q.getUserId());
                ps.setString(2, q.getQuestion());
                ps.setString(3, q.getAnswerHash());
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UserSecurityQuestion> getQuestionsByUserId(int userId) {
        List<UserSecurityQuestion> list = new ArrayList<>();
        String sql = "SELECT * FROM user_security_questions WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UserSecurityQuestion q = new UserSecurityQuestion();
                q.setId(rs.getInt("id"));
                q.setUserId(rs.getInt("user_id"));
                q.setQuestion(rs.getString("question"));
                q.setAnswerHash(rs.getString("answer_hash"));
                list.add(q);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean validateAnswers(int userId, List<String> answers) {
        List<UserSecurityQuestion> questions = getQuestionsByUserId(userId);

        if (questions.size() != answers.size()) return false;

        for (int i = 0; i < questions.size(); i++) {
            if (!PasswordUtil.verifyPassword(
                    answers.get(i),
                    questions.get(i).getAnswerHash())) {
                return false;
            }
        }
        return true;
    }
}
