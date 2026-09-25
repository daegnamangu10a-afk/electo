// package com.electo.electo.service;

// import java.security.SecureRandom;
// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.stereotype.Service;

// @Service
// public class OtpService {

//     private final SecureRandom random = new SecureRandom();

//     private final Map<String, String> otpStorage = new HashMap<>();
//     private final Map<String, Long> otpExpiry = new HashMap<>();

//     public String generateOTP(String email) {

//         int otpNumber = 100000 + random.nextInt(900000);
//         String otp = String.valueOf(otpNumber);

//         otpStorage.put(email.toLowerCase(), otp);

//         // OTP expires after 5 minutes
//         otpExpiry.put(
//             email.toLowerCase(),
//             System.currentTimeMillis() + (5 * 60 * 1000)
//         );

//         return otp;
//     }

//     public boolean verifyOTP(String email, String enteredOTP) {

//         String key = email.toLowerCase();

//         if (!otpStorage.containsKey(key)) {
//             return false;
//         }

//         if (System.currentTimeMillis() > otpExpiry.get(key)) {
//             otpStorage.remove(key);
//             otpExpiry.remove(key);
//             return false;
//         }

//         if (otpStorage.get(key).equals(enteredOTP)) {

//             // OTP can only be used once
//             otpStorage.remove(key);
//             otpExpiry.remove(key);

//             return true;
//         }

//         return false;
//     }
// }