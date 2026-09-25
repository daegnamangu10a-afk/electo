package com.electo.electo.controller;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electo.electo.dto.LoginRequest;
import com.electo.electo.dto.RegisterRequest;
import com.electo.electo.entity.Voter;
import com.electo.electo.repository.VoterRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final VoterRepository voterRepository;

    public AuthController(VoterRepository voterRepository) {
        this.voterRepository = voterRepository;
    }

    // =====================================================
    // REGISTRATION
    // =====================================================
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String name = request.getName() == null ? "" : request.getName().trim();
        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        String password = request.getPassword() == null ? "" : request.getPassword();

        // -------------------------
        // Required fields
        // -------------------------
        if (name.isEmpty()) {
            return ResponseEntity.badRequest().body("Full Name is required.");
        }

        if (email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }

        if (password.isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required.");
        }

        // -------------------------
        // Email validation
        // -------------------------
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (!Pattern.matches(emailRegex, email)) {
            return ResponseEntity.badRequest().body("Please enter a valid email address.");
        }

        // -------------------------
        // Password validation
        // Minimum 6 characters
        // -------------------------
        if (!Pattern.matches("^.{6,}$", password)) {
            return ResponseEntity.badRequest().body("Password must contain at least 6 characters.");
        }

        // -------------------------
        // Check existing user
        // -------------------------
        Optional<Voter> existingUser = voterRepository.findByEmailIgnoreCase(email);

        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists. Please login.");
        }

        // -------------------------
        // Create new user
        // -------------------------

        Voter voter = new Voter();
        voter.setName(name);
        voter.setEmail(email);
        voter.setPassword(password);

        // New registrations are VOTERS by default
        voter.setRole("VOTER");

        // Default voting status
        voter.setHasVoted(false);

        voterRepository.save(voter);

        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful.");
    }

    // =====================================================
    // LOGIN
    // =====================================================
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request) {

        String email = request.getEmail() == null
                ? ""
                : request.getEmail().trim();

        String password = request.getPassword() == null
                ? ""
                : request.getPassword();


        // -------------------------
        // Required fields
        // -------------------------

        if (email.isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Please enter your email address.");
        }


        if (password.isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Please enter your password.");
        }


        // -------------------------
        // Find user
        // -------------------------

        Optional<Voter> optionalUser =
                voterRepository.findByEmailIgnoreCase(email);


        if (optionalUser.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password.");
        }


        Voter user = optionalUser.get();


        // -------------------------
        // Check password
        // -------------------------

        if (user.getPassword() == null ||
                !user.getPassword().equals(password)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password.");
        }


        // -------------------------
        // Role handling
        // -------------------------

        String role = user.getRole();


        /*
         * ADMIN is the only special role.
         *
         * If role is:
         * ADMIN → Admin Dashboard
         *
         * Anything else / NULL / empty
         * → Voter Dashboard
         */

        if (role != null &&
                role.trim().equalsIgnoreCase("ADMIN")) {

            return ResponseEntity.ok("ADMIN");
        }


        // Default role = VOTER

        return ResponseEntity.ok("VOTER");
    }
}
