package vistager.service;

import vistager.model.User;

public interface AuthenticationService {
    User register(User request);

    String login(User request);

    String emailVerification(Long userId, String token);

    void resendEmailVerification(Long userId);

    void sendResetPasswordEmail(String email);

    String resetPassword(String email, String token, String password);
}
