package com.tradingplatform.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tradingplatform.modal.TwoFactorOTP;
import com.tradingplatform.modal.User;
import com.tradingplatform.repository.TwoFactorOtpRepository;

@Service
public class TwoFactorOtpServiceImpl implements TwoFactorServiceOtpService {
    @Autowired
    private TwoFactorOtpRepository twoFactorOtpRepository;

    @Override
    public TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt) {
       // Generate a unique ID for the OTP..
       //  UUID is a universally unique identifier that can be used to generate unique IDs for the OTPs
        UUID uuid = UUID.randomUUID();

        String id=uuid.toString();
        TwoFactorOTP twoFactorOTP=new TwoFactorOTP();
        twoFactorOTP.setOtp(otp);
        twoFactorOTP.setJwt(jwt);
        twoFactorOTP.setId(id);
        twoFactorOTP.setUser(user);


        return twoFactorOtpRepository.save(twoFactorOTP);
    }

    @Override
    public TwoFactorOTP findByUser(Long userId) {
        return twoFactorOtpRepository.findByUserId(userId);
    }

    @Override
    public TwoFactorOTP findById(String id) {
        Optional<TwoFactorOTP> optionalTwoFactorOTP = twoFactorOtpRepository.findById(id);
        return optionalTwoFactorOTP.orElse(null);
    }

    @Override
    public boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOtp, String otp) {
        return twoFactorOtp.getOtp().equals(otp);
    }

    @Override
    public void deleteTwoFactorOtp(TwoFactorOTP twoFactorOtp) {
        twoFactorOtpRepository.delete(twoFactorOtp);
    }

}
