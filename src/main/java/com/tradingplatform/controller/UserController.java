package com.tradingplatform.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tradingplatform.domain.VerificationType;
import com.tradingplatform.modal.ForgotPasswordToken;
import com.tradingplatform.modal.User;
import com.tradingplatform.modal.VerificationCode;
import com.tradingplatform.repository.UserRepository;
import com.tradingplatform.request.ForgotPasswordTokenRequest;
import com.tradingplatform.request.ResetPasswordRequest;
import com.tradingplatform.response.ApiResponse;
import com.tradingplatform.response.AuthResponse;
import com.tradingplatform.service.EmailService;
import com.tradingplatform.service.ForgotPasswordTokenService;
import com.tradingplatform.service.UserService;
import com.tradingplatform.service.VerificationCodeService;
import com.tradingplatform.utils.OtpUtils;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private ForgotPasswordTokenService forgotPasswordTokenService;

    @Autowired
    private UserRepository userRepository;

    // @GetMapping("/api/users/profile")
    // public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization")
    // String jwt) throws Exception{
    // User user = userService.findUserProfileByJwt(jwt);

    // return new ResponseEntity<User>(user,HttpStatus.OK);
    // }

    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(Authentication authentication) {
        String email = authentication.getName(); // ✅ JWT se aaya email
        User user = userService.findUserByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(@RequestHeader("Authorization") String jwt,
            @PathVariable VerificationType verificationType) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());
        if (verificationCode == null) {
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
        }
        if (verificationType.equals(VerificationType.EMAIL)) {
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }

        return new ResponseEntity<>("verification otp sent successfully ", HttpStatus.OK);
    }

    @PatchMapping("/api/users/enable-two-factor-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(@PathVariable String otp,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());
        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL)
                ? verificationCode.getEmail()
                : verificationCode.getMobile();

        boolean isVerified = verificationCode.getOtp().equals(otp);
        if (isVerified) {
            User updatedUser = userService.enableTwoFactorAuthentication(verificationCode.getVerificationType(), sendTo,
                    user);
            verificationCodeService.deleteVerificationCodeById(verificationCode);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        throw new Exception("wrong otp");
    }

    @PatchMapping("/api/users/disable-two-factor-otp/{otp}")
    public ResponseEntity<User> disableTwoFactorAuthentication(
            @PathVariable String otp,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        boolean isVerified = verificationCode.getOtp().equals(otp);

        if (isVerified) {

            User updatedUser = userService.disableTwoFactorAuthentication(user);

            verificationCodeService
                    .deleteVerificationCodeById(verificationCode);

            return new ResponseEntity<>(
                    updatedUser,
                    HttpStatus.OK);
        }

        throw new Exception("wrong otp");
    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(@RequestBody ForgotPasswordTokenRequest req)
            throws Exception {
        User user = userService.findUserByEmail(req.getSendTo());
        String otp = OtpUtils.generateOTP();
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        ForgotPasswordToken token = forgotPasswordTokenService.findByUser(user.getId());
        // if(token==null) {
        // token=forgotPasswordTokenService.createToken(user, id,otp,
        // req.getVerificationType(),req.getSendTo());
        // }

        // instead of above use this because it will not give new otp on requesting
        if (token != null) {
            forgotPasswordTokenService.deleteToken(token);
        }

        token = forgotPasswordTokenService.createToken(
                user,
                id,
                otp,
                req.getVerificationType(),
                req.getSendTo());

        if (req.getVerificationType().equals(VerificationType.EMAIL)) {
            emailService.sendVerificationOtpEmail(user.getEmail(), token.getOtp());
        }

        AuthResponse response = new AuthResponse();
        response.setSession(token.getId());
        response.setMessage("forgot password otp sent successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String id, @RequestBody ResetPasswordRequest req) //@RequestHeader("Authorization") String jwt comment becaue user will not have jwt for reseting its password 
            throws Exception {

        ForgotPasswordToken forgotPasswordToken = forgotPasswordTokenService.findById(id);
        ;
        boolean isVerified = forgotPasswordToken.getOtp().equals(req.getOtp());

        if (isVerified) {
            userService.updatePassword(forgotPasswordToken.getUser(), req.getPassword());
            ApiResponse response = new ApiResponse();
            response.setMessage("password reset successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        throw new Exception("wrong otp");
    }

    @PatchMapping("/api/users/profile")
    public ResponseEntity<User> updateProfile(
            @RequestHeader("Authorization") String jwt,
            @RequestBody User updatedUser) {

        User user = userService.findUserProfileByJwt(jwt);

        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setAddress(updatedUser.getAddress());
        user.setCity(updatedUser.getCity());
        user.setState(updatedUser.getState());
        user.setCountry(updatedUser.getCountry());
        user.setBio(updatedUser.getBio());
        user.setProfileImage(updatedUser.getProfileImage());

        User savedUser = userRepository.save(user);

        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }
}