package com.electo.electo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.electo.electo.entity.Candidate;
import com.electo.electo.repository.CandidateRepository;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin(origins = "*")
public class CandidateController {

    private final CandidateRepository candidateRepository;

    // Folder where candidate photos will be stored
    private final Path uploadDir =
            Paths.get("uploads").toAbsolutePath().normalize();

    public CandidateController(
            CandidateRepository candidateRepository) {

        this.candidateRepository = candidateRepository;
    }

    // =========================
    // GET CANDIDATES BY ELECTION
    // =========================

    @GetMapping("/election/{electionId}")
    public List<Candidate> getCandidatesByElection(
            @PathVariable Long electionId) {

        return candidateRepository.findByElectionId(electionId);
    }

    // =========================
    // GET CANDIDATES BY POSITION
    // =========================

    @GetMapping("/position/{positionId}")
    public List<Candidate> getCandidatesByPosition(
            @PathVariable Long positionId) {

        return candidateRepository.findByPositionId(positionId);
    }

    // =========================
    // UPLOAD CANDIDATE PHOTO
    // =========================

    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("file") MultipartFile file) {

        try {

            // Check if file exists
            if (file == null || file.isEmpty()) {

                return ResponseEntity.badRequest()
                        .body("Please select an image.");
            }

            // Check image type
            String contentType = file.getContentType();

            if (contentType == null ||
                    !contentType.startsWith("image/")) {

                return ResponseEntity.badRequest()
                        .body("Only image files are allowed.");
            }

            // Create uploads folder if it doesn't exist
            Files.createDirectories(uploadDir);

            // Get original filename
            String originalFilename =
                    file.getOriginalFilename();

            if (originalFilename == null ||
                    originalFilename.trim().isEmpty()) {

                return ResponseEntity.badRequest()
                        .body("Invalid file name.");
            }

            // Get file extension
            String extension = "";

            int dotIndex =
                    originalFilename.lastIndexOf(".");

            if (dotIndex >= 0) {
                extension =
                        originalFilename.substring(dotIndex);
            }

            // Create unique filename
            String newFilename =
                    UUID.randomUUID().toString() + extension;

            // Final file location
            Path targetLocation =
                    uploadDir.resolve(newFilename)
                            .normalize();

            // Save the actual image
            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // URL that frontend will use
            String imageUrl =
                    "/uploads/" + newFilename;

            return ResponseEntity.ok(imageUrl);

        } catch (IOException e) {

            return ResponseEntity.internalServerError()
                    .body("Could not upload image: "
                            + e.getMessage());
        }
    }

    // =========================
    // CREATE CANDIDATE
    // =========================

    @PostMapping
    public ResponseEntity<?> createCandidate(
            @RequestBody Candidate candidate) {

        if (candidate.getName() == null ||
                candidate.getName().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Candidate name is required.");
        }

        if (candidate.getElectionId() == null) {

            return ResponseEntity.badRequest()
                    .body("Election ID is required.");
        }

        if (candidate.getPositionId() == null) {

            return ResponseEntity.badRequest()
                    .body("Position ID is required.");
        }

        candidate.setName(
                candidate.getName().trim());

        Candidate savedCandidate =
                candidateRepository.save(candidate);

        return ResponseEntity.ok(savedCandidate);
    }

    // =========================
    // UPDATE CANDIDATE
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCandidate(
            @PathVariable Long id,
            @RequestBody Candidate updatedCandidate) {

        return candidateRepository.findById(id)
                .map(candidate -> {

                    candidate.setName(
                            updatedCandidate.getName());

                    candidate.setEmail(
                            updatedCandidate.getEmail());

                    candidate.setElectionId(
                            updatedCandidate.getElectionId());

                    candidate.setPositionId(
                            updatedCandidate.getPositionId());

                    candidate.setPhoto(
                            updatedCandidate.getPhoto());

                    candidate.setBiography(
                            updatedCandidate.getBiography());

                    Candidate savedCandidate =
                            candidateRepository.save(candidate);

                    return ResponseEntity.ok(savedCandidate);

                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    // =========================
    // DELETE CANDIDATE
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCandidate(
            @PathVariable Long id) {

        if (!candidateRepository.existsById(id)) {

            return ResponseEntity.notFound().build();
        }

        candidateRepository.deleteById(id);

        return ResponseEntity.ok(
                "Candidate deleted successfully.");
    }
}