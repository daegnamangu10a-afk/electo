package com.electo.electo;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOTP(String toEmail, String otp) {

        System.out.println("Preparing email...");
        System.out.println("Sending OTP to: " + toEmail);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Electo - Your OTP");

        message.setText(
                "Hello,\n\n" +
                "Your Electo verification OTP is:\n\n" +
                otp +
                "\n\n" +
                "This OTP is valid for a limited time.\n\n" +
                "Do not share this OTP with anyone.\n\n" +
                "Regards,\n" +
                "Electo Election Management System"
        );

        mailSender.send(message);

        System.out.println("Email sent successfully!");
    }
}