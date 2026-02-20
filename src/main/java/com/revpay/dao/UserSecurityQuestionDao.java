package com.revpay.dao;

import java.util.List;
import com.revpay.model.UserSecurityQuestion;

public interface UserSecurityQuestionDao {

    void saveSecurityQuestions(List<UserSecurityQuestion> questions);

    List<UserSecurityQuestion> getQuestionsByUserId(int userId);

    boolean validateAnswers(int userId, List<String> answers);
}
