package com.electo.electo;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin(origins = "*")
public class OtpController {

    private final OtpService otpService;
    private final EmailService emailService;
    private final VoterRepository voterRepository;

    public OtpController(
            OtpService otpService,
            EmailService emailService,
            VoterRepository voterRepository) {

        this.otpService = otpService;
        this.emailService = emailService;
        this.voterRepository = voterRepository;
    }

    @PostMapping("/send")
    public String sendOTP(@RequestParam("email") String email) {

        System.out.println("=================================");
        System.out.println("OTP REQUEST RECEIVED");
        System.out.println("EMAIL: " + email);

        try {

            String otp = otpService.generateOTP(email);

            System.out.println("OTP GENERATED: " + otp);

            emailService.sendOTP(email, otp);

            System.out.println("OTP EMAIL SENT SUCCESSFULLY");
            System.out.println("=================================");

            return "OTP sent successfully!";

        } catch (Exception e) {

            System.out.println("OTP ERROR: " + e.getMessage());
            e.printStackTrace();

            return "Failed to send OTP: " + e.getMessage();
        }
    }

@PostMapping("/verify")
public String verifyOTP(
        @RequestParam("email") String email,
        @RequestParam("otp") String otp) {

    System.out.println("OTP VERIFICATION REQUEST");
    System.out.println("EMAIL: " + email);
    System.out.println("OTP: " + otp);

    boolean verified = otpService.verifyOTP(email, otp);

    if (!verified) {
        return "Invalid or expired OTP.";
    }

    return voterRepository.findByEmailIgnoreCase(email)
            .map(voter -> {

                // Existing ADMIN
                if (voter.getRole() != null &&
                        voter.getRole().trim().equalsIgnoreCase("ADMIN")) {

                    return "ADMIN";
                }

                // Existing VOTER
                return "VOTER";
            })
            .orElseGet(() -> {

                // New email → create VOTER
                Voter newVoter = new Voter();

                newVoter.setEmail(email);
                newVoter.setRole("VOTER");
                newVoter.setHasVoted(false);

                voterRepository.save(newVoter);

                System.out.println(
                        "NEW VOTER CREATED: " + email
                );

                return "VOTER";
            });
    }
}
