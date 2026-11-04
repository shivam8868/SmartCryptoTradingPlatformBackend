package com.tradingplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationOtpEmail(String email,String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
        String subject = "Your OTP for Verification";
        String text = "Your OTP for verification is: " + otp; // or body istead of text
        System.out.println("OTP sending to => " + email);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(text);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setFrom("shivamkumarprajapati777@gmail.com");

        try {
            javaMailSender.send(mimeMessage);
        }catch (MailException e) {
            e.printStackTrace();
            throw  new MailSendException("Failed to send email: " + e.getMessage());
        }
        
    }
}
