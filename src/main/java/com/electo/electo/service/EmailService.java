// package com.electo.electo.service;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;

// import com.resend.Resend;
// import com.resend.core.exception.ResendException;
// import com.resend.services.emails.model.CreateEmailOptions;

// @Service
// public class EmailService {

//     @Value("${resend.api.key}")
//     private String resendApiKey;

//     public void sendOTP(String toEmail, String otp) {

//         try {

//             Resend resend = new Resend(resendApiKey);

//             CreateEmailOptions params = CreateEmailOptions.builder()
//                     .from("noreply@electo.com")
//                     .to(toEmail)
//                     .subject("Electo - Your OTP")
//                     .html(
//                             "<h2>Electo - Election Management System</h2>" +
//                             "<p>Your One-Time Password (OTP) is:</p>" +
//                             "<h1>" + otp + "</h1>" +
//                             "<p>This OTP is valid for 5 minutes.</p>" +
//                             "<p>Please do not share this OTP with anyone.</p>"
//                     )
//                     .build();

//             resend.emails().send(params);

//             System.out.println("OTP email sent successfully to: " + toEmail);

//         } catch (ResendException e) {

//             System.out.println("Failed to send OTP email.");
//             e.printStackTrace();

//             throw new RuntimeException("Failed to send OTP email", e);
//         }
//     }
// }