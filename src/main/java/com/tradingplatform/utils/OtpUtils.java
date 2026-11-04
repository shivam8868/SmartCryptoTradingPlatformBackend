package com.tradingplatform.utils;

import java.util.Random;

public class OtpUtils {

    public static String generateOTP() {
        // Generate a random 6-digit OTP
        // int otp = (int)(Math.random() * 900000) + 100000;
        // return String.valueOf(otp);

        int otpLength=6;
        Random random=new Random();
        StringBuilder otp=  new StringBuilder(otpLength);
        for(int i=0;i<otpLength;i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();

    }
}
