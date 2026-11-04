package com.tradingplatform.service;

import com.tradingplatform.domain.VerificationType;
import com.tradingplatform.modal.User;

public interface UserService {
    public User findUserProfileByJwt(String jwt);
    public User findUserByEmail(String email);
    public User findUserById(Long userId);
    public User enableTwoFactorAuthentication(VerificationType verificationType,String sendTo,User user);
    public User disableTwoFactorAuthentication(User user);
    User updatePassword(User user, String newPassword);
}
