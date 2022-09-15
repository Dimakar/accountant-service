package ru.dimakar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {
    @Autowired
    UserService userService;

    private final int MAX_ATTEMPT = 5;
    private final int MAX_ATTEMPT_FOR_ADMIN = 50;

    public void loginSucceeded(String login) {
        userService.clearFailureCount(login);
    }

    public void loginFailed(String login) {
        userService.incrementFailedAttempts(login, MAX_ATTEMPT, MAX_ATTEMPT_FOR_ADMIN);
    }
}