package com.tradingplatform.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.tradingplatform.config.JwtProvider;
import com.tradingplatform.domain.VerificationType;
import com.tradingplatform.modal.TwoFactorAuth;
import com.tradingplatform.modal.User;
import com.tradingplatform.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findUserProfileByJwt(String jwt) {
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7); // remove "Bearer "
        }
        String email = JwtProvider.getEmailFromToken(jwt);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return user;

    }

    @Override
    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return user;

    }

    // @Override
    // public User findUserByEmail(String email) {
    // return userRepository.findByEmail(email)
    // .orElseThrow(() -> new RuntimeException("User not found with email: " +
    // email));
    // }

    @Override
    public User findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return user.get();
    }

    @Override
    public User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user) {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSendTo(verificationType);
        user.setTwoFactorAuth(twoFactorAuth);
        return userRepository.save(user);
    }

    @Override
    public User disableTwoFactorAuthentication(User user) {
        TwoFactorAuth twoFactorAuth= user.getTwoFactorAuth();
        twoFactorAuth.setEnabled(false);
        user.setTwoFactorAuth(twoFactorAuth);  
        return userRepository.save(user);
    }
    @Override
    public User updatePassword(User user, String newPassword) {
        // user.setPassword(newPassword);
        user.setPassword(
    passwordEncoder.encode(newPassword)
); // use this ffor ffuture onwards ok bcrytpion ke liye 
        return userRepository.save(user);
    }
}